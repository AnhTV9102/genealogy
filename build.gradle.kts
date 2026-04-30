plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "6.25.0"
	id("checkstyle")
	id("pmd")
}

group = "com.genealogy"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {

	// 🔹 Core
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// 🔹 Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// 🔹 Data
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// 🔹 DB
	runtimeOnly("org.postgresql:postgresql")

	// 🔹 Migration
	implementation("org.flywaydb:flyway-core")
	runtimeOnly("org.flywaydb:flyway-database-postgresql")

	// 🔹 JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

	// 🔹 Monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// 🔹 Dev
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// 🔹 Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// 🔹 Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

spotless {
	java {
		googleJavaFormat("1.17.0")
		target("src/**/*.java")
		trimTrailingWhitespace()
		endWithNewline()
	}
}

checkstyle {
	toolVersion = "10.12.4"
	configDirectory.set(rootProject.file("config/checkstyle"))

	configProperties["checkstyle.suppressions.file"] =
		rootProject.file("config/checkstyle/suppressions.xml").absolutePath
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		named("xml") { required.set(true) }
		named("html") { required.set(true) }
	}
}

pmd {
	toolVersion = "6.55.0"
	ruleSets = listOf()
	ruleSetFiles = files("config/pmd/ruleset.xml")
}

tasks.withType<Pmd>().configureEach {
	reports {
		named("xml") { required.set(true) }
		named("html") { required.set(true) }
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(21)
}

tasks.named("check") {
	dependsOn("spotlessCheck")
}

tasks.test {
	useJUnitPlatform()
}
