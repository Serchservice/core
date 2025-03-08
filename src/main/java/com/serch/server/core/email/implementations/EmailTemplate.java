package com.serch.server.core.email.implementations;

import com.serch.server.core.email.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailTemplate implements EmailTemplateService {
    private final TemplateEngine engine;

    @Override
    public String adminInvite(String inviteeName, String inviterName, String inviteLink) {
        Context context = new Context();
        context.setVariable("inviteeName", inviteeName);
        context.setVariable("inviterName", inviterName);
        context.setVariable("inviteLink", inviteLink);

        return engine.process("admin-invite", context);
    }

    @Override
    public String signup(String code, boolean isNearby) {
        Context context = new Context();
        context.setVariable("code", code);

        return engine.process(isNearby ? "nearby-signup" : "signup", context);
    }

    @Override
    public String adminLogin(String name, String code) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("name", name);

        return engine.process("admin-login", context);
    }

    @Override
    public String adminSignup(String name, String code) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("name", name);

        return engine.process("admin-signup", context);
    }

    @Override
    public String adminResetPassword(String name, String resetLink) {
        Context context = new Context();
        context.setVariable("resetLink", resetLink);
        context.setVariable("name", name);

        return engine.process("admin-password-reset", context);
    }

    @Override
    public String resetPassword(String code, boolean isNearby) {
        Context context = new Context();
        context.setVariable("code", code);

        return engine.process(isNearby ? "nearby-reset-password" : "reset-password", context);
    }

    @Override
    public String associateInvite(String name, String businessName, String businessAdminName, String businessLogo, String businessDescription, String businessCategory, String inviteLink) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("businessName", businessName);
        context.setVariable("businessLogo", businessLogo != null && !businessLogo.isEmpty() ? businessLogo : "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/serch/logo/logo.png");
        context.setVariable("businessAdminName", businessAdminName);
        context.setVariable("businessDescription", businessDescription);
        context.setVariable("businessCategory", businessCategory);
        context.setVariable("inviteLink", inviteLink);

        return engine.process("associate-invite", context);
    }
}
