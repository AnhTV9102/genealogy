package com.genealogy.common.config;

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
 * Class này sẽ chuẩn hóa {@code spring.datasource.url} ngay từ giai đoạn boot sớm.
 */
public class DataSourceUrlNormalizerEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty("spring.datasource.url");
        if (url == null) {
            return;
        }

        String trimmed = url.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("jdbc:")) {
            return;
        }

        String normalized = normalize(trimmed);
        if (normalized.equals(trimmed)) {
            return;
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new MapPropertySource("datasourceUrlNormalizer", Map.of("spring.datasource.url", normalized)));
    }

    private static String normalize(String url) {
        if (url.startsWith("postgres://")) {
            // Render thường trả về "postgres://..." => driver cần "jdbc:postgresql://..."
            return "jdbc:postgresql://" + url.substring("postgres://".length());
        }

        if (url.startsWith("postgresql://")) {
            // "postgresql://..." => "jdbc:postgresql://..."
            return "jdbc:" + url;
        }

        // Fallback: chỉ thêm "jdbc:" để tránh crash sớm.
        return "jdbc:" + url;
    }
}

