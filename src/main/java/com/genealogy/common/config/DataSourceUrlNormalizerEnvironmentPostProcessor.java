package com.genealogy.common.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * Render (hoặc một số provider) có thể cung cấp URL dạng PostgreSQL không có tiền tố "jdbc:".
 * Spring Boot/Hikari/Flyway yêu cầu datasource URL phải bắt đầu bằng "jdbc:".
 *
 * <p>Class này sẽ chuẩn hóa {@code spring.datasource.url} ngay từ giai đoạn boot sớm.
 */
public class DataSourceUrlNormalizerEnvironmentPostProcessor implements EnvironmentPostProcessor {

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    String url = environment.getProperty("spring.datasource.url");
    if (url == null) {
      return;
    }

    String trimmed = url.trim();
    if (trimmed.isEmpty() || trimmed.startsWith("jdbc:")) {
      return;
    }

    String defaultPort = environment.getProperty("DB_PORT", "5432");
    String normalized = normalize(trimmed, defaultPort);
    if (normalized.equals(trimmed)) {
      return;
    }

    MutablePropertySources propertySources = environment.getPropertySources();
    propertySources.addFirst(
        new MapPropertySource(
            "datasourceUrlNormalizer", Map.of("spring.datasource.url", normalized)));
  }

  private static String normalize(String url, String defaultPort) {
    if (url.startsWith("jdbc:")) {
      return url;
    }

    if (!url.startsWith("postgres://") && !url.startsWith("postgresql://")) {
      // Fallback: chỉ thêm "jdbc:" để tránh crash sớm.
      return "jdbc:" + url;
    }

    // Expect common Render format:
    // postgres://<user>:<password>@<host>[:<port>]/<db>[?<query>]
    // JDBC format preferred by the PostgreSQL driver:
    // jdbc:postgresql://<host>:<port>/<db>?user=<user>&password=<password>[&<query>]
    String schemePrefix = url.startsWith("postgresql://") ? "postgresql://" : "postgres://";
    String remainder = url.substring(schemePrefix.length());

    int slashIndex = remainder.indexOf('/');
    if (slashIndex < 0) {
      return "jdbc:" + url;
    }

    String authority = remainder.substring(0, slashIndex);
    String pathAndQuery = remainder.substring(slashIndex + 1);

    String dbName = pathAndQuery;
    String existingQuery = "";
    int qIndex = pathAndQuery.indexOf('?');
    if (qIndex >= 0) {
      dbName = pathAndQuery.substring(0, qIndex);
      existingQuery = pathAndQuery.substring(qIndex + 1);
    }

    String user = null;
    String password = null;
    String hostPort = authority;

    int atIndex = authority.lastIndexOf('@');
    if (atIndex >= 0) {
      String userInfo = authority.substring(0, atIndex);
      hostPort = authority.substring(atIndex + 1);

      // Password có thể chứa ":" nên cần tách theo "dấu : cuối"
      int colonInUser = userInfo.lastIndexOf(':');
      if (colonInUser >= 0) {
        user = userInfo.substring(0, colonInUser);
        password = userInfo.substring(colonInUser + 1);
      } else {
        user = userInfo;
      }
    }

    // Parse host and port from hostPort (no IPv6 expected in Render URLs).
    String host = hostPort;
    String port = null;
    int lastColon = hostPort.lastIndexOf(':');
    if (lastColon > 0) {
      String maybePort = hostPort.substring(lastColon + 1);
      if (!maybePort.isEmpty() && maybePort.chars().allMatch(Character::isDigit)) {
        host = hostPort.substring(0, lastColon);
        port = maybePort;
      }
    }

    if (port == null || port.isBlank()) {
      port = defaultPort;
    }

    StringBuilder jdbc = new StringBuilder();
    jdbc.append("jdbc:postgresql://")
        .append(host)
        .append(':')
        .append(port)
        .append('/')
        .append(dbName);

    // Merge query params: keep any existing query (e.g. sslmode), then append user/password if
    // missing.
    StringBuilder query = new StringBuilder();
    if (!existingQuery.isBlank()) {
      query.append(existingQuery);
    }

    if (user != null && !user.isBlank()) {
      if (query.length() > 0) query.append('&');
      query.append("user=").append(urlEncodeQueryParam(user));
    }
    if (password != null && !password.isBlank()) {
      if (query.length() > 0) query.append('&');
      query.append("password=").append(urlEncodeQueryParam(password));
    }

    if (query.length() > 0) {
      jdbc.append('?').append(query);
    }

    return jdbc.toString();
  }

  private static String urlEncodeQueryParam(String value) {
    // URLEncoder dùng '+' cho space, nhưng query string thường muốn '%20'.
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
