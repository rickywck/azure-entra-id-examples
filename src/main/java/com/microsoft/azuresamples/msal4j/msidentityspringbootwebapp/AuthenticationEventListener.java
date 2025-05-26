package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.lang.NonNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class AuthenticationEventListener implements ApplicationListener<ApplicationEvent> {
    private static final String UNKNOWN = "unknown";
    private final LoginAttemptLogger loginAttemptLogger;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public AuthenticationEventListener(LoginAttemptLogger loginAttemptLogger) {
        this.loginAttemptLogger = loginAttemptLogger;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        String loginId = UNKNOWN;
        String status;
        if (event instanceof AuthenticationSuccessEvent successEvent) {
            if (successEvent.getAuthentication() != null && successEvent.getAuthentication().getName() != null) {
                loginId = successEvent.getAuthentication().getName();
            }
            status = "SUCCESS";
        } else if (event instanceof AbstractAuthenticationFailureEvent failureEvent) {
            if (failureEvent.getAuthentication() != null && failureEvent.getAuthentication().getName() != null) {
                loginId = failureEvent.getAuthentication().getName();
            }
            status = "FAILURE";
        } else {
            return; // Do not log other types of authentication events
        }
        String ipAddress = getClientIpAddress();
        String currentDateTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMATTER);
        loginAttemptLogger.log(loginId, currentDateTimeUTC, ipAddress, status);
    }

    private String getClientIpAddress() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest().getRemoteAddr();
        }
        return UNKNOWN;
    }
}
