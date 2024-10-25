# Changelog

Version: **1.0.8**

SpringBoot Version: **3.2.8**

All notable changes to this project will be documented in this file.

## [Unreleased]

### ✍️ Commit Messages

* 987099d Fixing errors and adding new endpoints

### ✨ Added

* `src/main/java/com/serch/server/admin/enums/SocialMediaPlatform.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/mappers/ScopeMapper.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/models/Organization.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/models/transaction/Payout.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/models/transaction/ResolvedTransaction.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/repositories/OrganizationRepository.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/repositories/transactions/PayoutRepository.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/repositories/transactions/ResolvedTransactionRepository.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/organization/OrganizationController.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/organization/data/OrganizationDto.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/organization/data/OrganizationResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/organization/services/OrganizationImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/organization/services/OrganizationService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/common/CommonProfileImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/common/CommonProfileService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/controllers/PayoutScopeController.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/controllers/TransactionScopeController.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/controllers/WalletScopeController.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/payouts/PayoutResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/payouts/PayoutScopeResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/transactions/PaymentApiResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/transactions/ResolvedTransactionResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/transactions/TransactionGroupScopeResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/transactions/TransactionScopeResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/transactions/TransactionTypeResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/responses/wallet/WalletScopeResponse.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/PaymentScopeService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/PayoutScopeService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/RevenueScopeService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/TransactionScopeService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/WalletScopeService.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/implementations/PayoutScopeImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/implementations/TransactionScopeImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/services/scopes/payment/services/implementations/WalletScopeImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/validator/SocialMediaLinkValidator.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/admin/validator/ValidSocialMediaLink.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/core/qr_code/QRCodeImplementation.java` - This file introduces new functionality or features to enhance the user experience.
* `src/main/java/com/serch/server/core/qr_code/QRCodeService.java` - This file introduces new functionality or features to enhance the user experience.

### 🛠️ Modified

* `README.md` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `doc/documentation.yaml` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `pom.xml` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/enums/PermissionScope.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/mappers/AdminMapper.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/mappers/CommonMapper.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/account/services/AdminActivityService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/account/services/AdminProfileService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/account/services/TeamService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/auth/AdminAuthService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/permission/services/GrantedPermissionService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/permission/services/PermissionImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/permission/services/PermissionService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/projections/MetricProjection.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/responses/CommonProfileResponse.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/admin/AdminScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/common/CommonAccountAnalysisService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/common/CommonAuthImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/common/CommonAuthService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/features/services/CallScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/features/services/ProvideSharingScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/features/services/RequestSharingScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/features/services/ShopScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/features/services/implementations/ShopScopeImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/ComplaintScopeImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/ComplaintScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/SpeakWithSerchScopeImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/SpeakWithSerchScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/SupportScopeImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/admin/services/scopes/support/services/SupportScopeService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/configurations/LogoutService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/configurations/SecurityFilterConfiguration.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/configurations/SwaggerConfiguration.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/code/TokenService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/email/EmailService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/email/templates/EmailTemplateService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/jwt/JwtService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/map/services/LocationService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/notification/core/NotificationCoreService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/notification/core/NotificationImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/notification/core/NotificationService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/payment/core/PaymentService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/session/SessionImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/session/SessionService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/sms/implementation/SmsService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/socket/SocketService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/storage/core/StorageService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/totp/MFAAuthenticator.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/totp/MFAAuthenticatorService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/core/totp/TOTP.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/models/transaction/Transaction.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/repositories/auth/RefreshTokenRepository.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/repositories/auth/SessionRepository.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/repositories/auth/UserRepository.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/repositories/transaction/TransactionRepository.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/repositories/trip/ActiveRepository.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/account/controllers/BusinessAssociateAuthController.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/account/services/BusinessAssociateAuthService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/account/services/implementations/BusinessAssociateAuthImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/auth/requests/RequestSession.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/auth/services/implementations/AuthImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/auth/services/implementations/MFAImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/certificate/services/CertificateImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/conversation/services/CallImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/conversation/services/ChattingImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/ShopController.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/ShopDriveController.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/services/ShopDriveImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/services/ShopDriveService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/services/ShopImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/shop/services/ShopService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/transaction/services/BankService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/transaction/services/SchedulePayService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/transaction/services/WalletService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/transaction/services/implementations/WalletImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/ActiveSearchService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/ActiveService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripAuthenticationService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripHistoryService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripPayService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripRequestService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripShareService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/TripTimelineService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/trip/services/implementations/TripPayImplementation.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/java/com/serch/server/services/verified/services/VerificationService.java` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/admin-invite.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/admin-login.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/admin-password-reset.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/admin-signup.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/associate-invite.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/reset-password.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.
* `src/main/resources/templates/signup.html` - This file has been updated to improve functionality or fix issues, ensuring a better performance.

### ❌ Deleted

* `src/main/java/com/serch/server/services/transaction/responses/RecentTransaction.java` - This file has been removed as it was no longer necessary or has been replaced by better alternatives.

