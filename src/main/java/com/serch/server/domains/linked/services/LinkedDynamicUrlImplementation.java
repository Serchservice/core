package com.serch.server.domains.linked.services;

import com.serch.server.domains.linked.dtos.LinkedDynamicUrlDto;
import com.serch.server.domains.linked.mappers.LinkedDynamicUrlMapper;
import com.serch.server.domains.linked.models.LinkedDynamicUrl;
import com.serch.server.domains.linked.models.LinkedDynamicUrlRepository;
import com.serch.server.exceptions.others.LinkedDynamicException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class LinkedDynamicUrlImplementation implements LinkedDynamicUrlService {
    private final TemplateEngine templateEngine;
    private final LinkedDynamicUrlRepository linkedDynamicUrlRepository;

    private String builder(String identifier) {
        return "https://to.serchservice.com/%s".formatted(identifier);
    }

    @Override
    public String generate(LinkedDynamicUrlDto request) {
        return builder(build(request).getIdentifier());
    }

    @Override
    public LinkedDynamicUrlDto build(LinkedDynamicUrlDto request) {
        LinkedDynamicUrl url = linkedDynamicUrlRepository.findByIdentifier(request.getIdentifier())
                .orElseGet(() -> linkedDynamicUrlRepository.save(LinkedDynamicUrlMapper.instance.url(request)));

        return LinkedDynamicUrlMapper.instance.dto(url);
    }

    @Override
    public String request(String identifier) {
        LinkedDynamicUrl url = linkedDynamicUrlRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new LinkedDynamicException("404 Error: Link not found"));

        return generatePreview(url);
    }

    private String generatePreview(LinkedDynamicUrl url) {
        Context context = new Context();
        context.setVariable("title", url.getTitle());
        context.setVariable("description", url.getDescription());
        context.setVariable("thumbnail", url.getContentUrl());
        context.setVariable("redirectToStore", url.getRedirectToStore());
        context.setVariable("redirectUrl", url.getRedirectUrl());
        context.setVariable("androidScheme", url.getAndroidScheme());
        context.setVariable("androidBundleID", url.getAndroidBundleId());
        context.setVariable("appStoreID", url.getIosBundleId());
        context.setVariable("shortUrl", builder(url.getIdentifier()));
        context.setVariable("androidUrl", "https://play.google.com/store/apps/details?id=%s".formatted(url.getAndroidBundleId()));
        context.setVariable("favicon", "https://chxpalpeslofqzeulcjr.supabase.co/storage/v1/object/public/serch/nearby/favicon.png");

        return templateEngine.process("nearby-dynamic-url", context);
    }

    @Override
    public String preview(String identifier, String userAgent, HttpServletResponse response) {
        LinkedDynamicUrl url = linkedDynamicUrlRepository.findByIdentifier(identifier).orElse(null);

        try {
            if(url != null) {
                if(isBot(userAgent)) {
                    return generatePreview(url);
                } else {
                    response.sendRedirect(url.getRedirectUrl());
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    private boolean isBot(String userAgent) {
        return userAgent != null && userAgent.toLowerCase().matches(".*(facebook|twitter|whatsapp|linkedin|bot|crawler|spider).*");
    }
}