package com.serch.server.domains.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.company.Newsletter;
import com.serch.server.repositories.company.NewsletterRepository;
import com.serch.server.domains.company.services.NewsletterService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.DomainNameUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Service
@RequiredArgsConstructor
public class NewsletterImplementation implements NewsletterService {
    private final NewsletterRepository newsletterRepository;

    private static final int MAX_LOCAL_PART_LENGTH = 64;

    private static final String LOCAL_PART_ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uFFFF-]";

    private static final String LOCAL_PART_INSIDE_QUOTES_ATOM = "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uFFFF-]|\\\\\\\\|\\\\\\\")";

    /**
     * Regular expression for the local part of an email address (everything before '@')
     */
    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile(
            "(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" +
                    "(?:\\." + "(?:" + LOCAL_PART_ATOM + "+|\"" + LOCAL_PART_INSIDE_QUOTES_ATOM + "+\")" + ")*", CASE_INSENSITIVE
    );

    private boolean isValid(String emailAddress) {
        Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        if(emailAddress != null) {
            int splitPosition = emailAddress.lastIndexOf( '@' );
            // need to check if there is @
            if ( splitPosition < 0 ) {
                return false;
            }

            String localPart = emailAddress.substring( 0, splitPosition );
            String domainPart = emailAddress.substring( splitPosition + 1 );

            if (!isValidEmailLocalPart(localPart)) {
                return false;
            }
            return DomainNameUtil.isValidEmailDomainAddress( domainPart ) && EMAIL_PATTERN.matcher(emailAddress).matches();
        } else {
            return false;
        }
    }

    private boolean isValidEmailLocalPart(String localPart) {
        if ( localPart.length() > MAX_LOCAL_PART_LENGTH ) {
            return false;
        }
        Matcher matcher = LOCAL_PART_PATTERN.matcher( localPart );

        return matcher.matches();
    }

    @Override
    public ApiResponse<String> subscribe(String emailAddress) {
        if(emailAddress == null || emailAddress.isEmpty()) {
            return new ApiResponse<>("Email address must not be empty");
        } else if(isValid(emailAddress)) {
            AtomicReference<String> message = new AtomicReference<>("Subscribed");
            newsletterRepository.findByEmailAddressIgnoreCase(emailAddress)
                    .ifPresentOrElse(newsletter -> message.set("You already have an existing subscription"), () -> {
                        Newsletter newsletter = new Newsletter();
                        newsletter.setEmailAddress(emailAddress);
                        newsletterRepository.save(newsletter);
                    });

            return new ApiResponse<>(message.get(), HttpStatus.OK);
        } else {
            return new ApiResponse<>("Invalid email address");
        }
    }

    @Override
    public ApiResponse<String> unsubscribe(String emailAddress) {
        if(emailAddress == null || emailAddress.isEmpty()) {
            return new ApiResponse<>("Email address must not be empty");
        } else if(isValid(emailAddress)) {
            newsletterRepository.findByEmailAddressIgnoreCase(emailAddress).ifPresent(newsletterRepository::delete);
            return new ApiResponse<>("Unsubscribed", HttpStatus.OK);
        } else {
            return new ApiResponse<>("Invalid email address");
        }
    }
}