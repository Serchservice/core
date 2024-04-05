package com.serch.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
		"spring.application.name=server",
		"### SERVER",
		"server.port=8020",
		"server.servlet.context-path=/api/v1",
		"server.error.include-message=always",
		"### SPRING DATASOURCE",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.password=Password",
		"spring.datasource.username=Evaristus",
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL;INIT=CREATE SCHEMA IF NOT EXISTS company",
		"### SPRING JPA",
		"spring.jpa.hibernate.ddl-auto=update",
		"spring.jpa.generate-ddl=true",
		"spring.jpa.open-in-view=true",
		"spring.jpa.show-sql=true",
		"## ANOTHER SPRING SETTING",
		"spring.sql.init.mode=always",
		"spring.data.jpa.repositories.enabled=true",
		"spring.devtools.add-properties=true",
		"### MULTIPART CONFIGURATION",
		"spring.servlet.multipart.max-file-size=10MB",
		"spring.servlet.multipart.max-request-size=10MB",
		"spring.codec.max-in-memory-size=10MB",
		"### CORS CONFIGURATION",
		"#management.endpoints.web.cors.allow-credentials=true",
		"management.endpoints.web.cors.allowed-headers=*",
		"management.endpoints.web.cors.allowed-methods=*",
		"management.endpoints.web.cors.allowed-origin-patterns={http://localhost:3000, *}",
		"management.endpoints.web.cors.allowed-origins=*",
})
class ServerApplicationTests {
	@Test
	void contextLoads() {
	}
}