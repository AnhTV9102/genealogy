plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
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
//	implementation("org.springframework.boot:spring-boot-starter-security")

	// 🔹 Data
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	// 🔹 DB
	runtimeOnly("org.postgresql:postgresql")

	// 🔹 Migration
	implementation("org.flywaydb:flyway-core")

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
}

tasks.withType<Test> {
	useJUnitPlatform()
}
