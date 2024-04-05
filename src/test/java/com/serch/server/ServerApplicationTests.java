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
		"server.port=${SERVER_PORT}",
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
		"### EMAIL SMTP",
		"spring.mail.host=${MAIL_HOST}",
		"spring.mail.port=${MAIL_PORT}",
		"spring.mail.username=${MAIL_USERNAME}",
		"spring.mail.password=${MAIL_PASSWORD}",
		"spring.mail.properties.mail.smtp.auth=true",
		"spring.mail.properties.mail.smtp.starttls.enable=true",
		"### SERCH APPLICATION SETTINGS",
		"### APPLICATION SETTINGS - JWT",
		"application.security.jwt-secret-key=${JWT_SECRET_KEY}",
		"application.security.jwt-expiration-time=${JWT_EXPIRATION_TIME}",
		"### APPLICATION SETTINGS - One-Time Password",
		"application.security.otp-token-length=${OTP_TOKEN_LENGTH}",
		"application.security.otp-expiration-time=${OTP_EXPIRATION_TIME}",
		"application.security.otp-token-characters=${OTP_TOKEN_CHARACTERS}",
		"### APPLICATION SETTINGS - Refresh Token",
		"application.security.refresh-token-length=${REFRESH_TOKEN_LENGTH}",
		"application.security.refresh-token-characters=${REFRESH_TOKEN_CHARACTERS}",
		"application.settings.specialty-limit=${SPECIALTY_LIMIT}",
		"application.settings.account-duration=${ACCOUNT_DURATION}",
		"### APPLICATION SETTINGS - FIREBASE",
		"application.firebase.config=firebase.json",
		"### SERCH SETTINGS",
		"### SERCH - PAYSTACK",
		"serch.paystack.test-secret-key=${PS_TEST_SECRET}",
		"serch.paystack.test-public-key=${PS_TEST_PUBLIC}",
		"serch.paystack.live-secret-key=${PS_LIVE_SECRET}",
		"serch.paystack.live-public-key=${PS_LIVE_PUBLIC}",
		"### SERCH - AGORA",
		"serch.agora.app-id=${AGORA_APP_ID}",
		"serch.tip2fix.call-limit=${TIP2FIX_CALL_LIMIT}",
		"### RESEND API KEY",
		"serch.resend.mail-api-key=${RESEND_API_KEY}",
		"### SERCH - GOOGLE",
		"serch.map-api-key=${MAP_API_KEY}",
		"serch.map-search-radius=${MAP_SEARCH_RADIUS}",
		"### SERCH - AWS",
		"serch.aws.secret-key=${AWS_SECRET_KEY}",
		"serch.aws.secret-access-key=${AWS_SECRET_ACCESS_KEY}",
		"serch.aws.region=${AWS_REGION}",
		"serch.aws.bucket.platform=${AWS_BUCKET_PLATFORM}",
		"serch.aws.bucket.asset=${AWS_BUCKET_SERCH_ASSET}",
		"### SERCH - WALLET SETTINGS",
		"serch.wallet.fund-amount-limit=${FUND_WALLET_AMOUNT_LIMIT}",
		"serch.wallet.withdraw-amount-limit=${WITHDRAW_AMOUNT_LIMIT}"
})
class ServerApplicationTests {
	@Test
	void contextLoads() {
	}
}