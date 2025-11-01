plugins {
	java
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
}

group = "com.hotel"
version = "0.0.1-SNAPSHOT"
description = "Hotel Demo Service with Email Order Search"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("hotel-demo-service.jar")
}


repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	// Spring Boot starters
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-cassandra")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	
	// Fuzzy matching
	implementation("org.apache.commons:commons-text:1.11.0")
	
	// MCP Tool Support
	implementation("org.springframework.ai:spring-ai-starter-mcp-server:1.1.0-M3")
	
	// Encryption
	implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
	
	// Logging
	implementation("net.logstash.logback:logstash-logback-encoder:7.4")
	
	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:testcontainers:1.19.3")
	testImplementation("org.testcontainers:cassandra:1.19.3")
	testImplementation("org.testcontainers:junit-jupiter:1.19.3")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
	toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.95".toBigDecimal()
			}
		}
	}
}
