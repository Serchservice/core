package com.serch.server.core.email.implementations;

import com.serch.server.core.email.GoEmailTemplateService;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class GoEmailTemplate implements GoEmailTemplateService {
    private final TemplateEngine engine;

    @Override
    public String failed(GoUserAddon addon, String errorMessage) {
        Context context = new Context();
        context.setVariable("error", errorMessage);

        return getTemplate(addon, context, "nearby-addon-charge-failed");
    }

    private String getTemplate(GoUserAddon addon, Context context, String template) {
        context.setVariable("addon", addon.getPlan().getAddon().getName());
        context.setVariable("plan", addon.getPlan().getName());
        context.setVariable("interval", addon.getPlan().getInterval().getValue());
        context.setVariable("status", addon.getStatus().name().replaceAll("_", " "));
        context.setVariable("trials", addon.getTrials());
        context.setVariable("amount", addon.getPlan().getAmt());
        context.setVariable("recurring", addon.getIsRecurring());

        return engine.process(template, context);
    }

    @Override
    public String success(GoUserAddon addon, String reference) {
        Context context = new Context();
        context.setVariable("reference", reference);
        context.setVariable("next", TimeUtil.formatDate(addon.getUser().getTimezone(), addon.getNextBillingDate()));

        return getTemplate(addon, context, "nearby-addon-charge-success");
    }

    @Override
    public String invoice(GoUserAddon addon) {
        Context context = new Context();

        return getTemplate(addon, context, "nearby-addon-invoice");
    }

    @Override
    public String invoiceSwitch(GoUserAddon addon) {
        Context context = getNewAddonContext(addon);

        return getTemplate(addon, context, "nearby-addon-new-details");
    }

    private Context getNewAddonContext(GoUserAddon addon) {
        Context context = new Context();
        context.setVariable("newAddon", addon.getChange().getPlan().getAddon().getName());
        context.setVariable("newPlan", addon.getChange().getPlan().getName());
        context.setVariable("newInterval", addon.getChange().getPlan().getInterval().getValue());
        context.setVariable("newAmount", addon.getChange().getPlan().getAmt());
        context.setVariable("newRecurring", addon.getChange().getPlan().isRecurring());

        return context;
    }

    @Override
    public String pending(GoUserAddon addon, String link) {
        Context context = getNewAddonContext(addon);
        context.setVariable("payLink", link);

        return getTemplate(addon, context, "nearby-addon-charge-pending");
    }
}