package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class AuthenticationEventListener implements ApplicationListener<AuthenticationEvent> {

    @Autowired
    private LoginAttemptLogger loginAttemptLogger;

    @Autowired
    private HttpServletRequest request;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void onApplicationEvent(AuthenticationEvent event) {
        String loginId = "unknown";
        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            loginId = event.getAuthentication().getName();
        }

        String ipAddress = "unknown";
        if (request != null && request.getRemoteAddr() != null) {
            ipAddress = request.getRemoteAddr();
        }

        String currentDateTimeUTC = OffsetDateTime.now(ZoneOffset.UTC).format(DATE_TIME_FORMATTER);
        String status;

        if (event instanceof AuthenticationSuccessEvent) {
            status = "SUCCESS";
        } else if (event instanceof AbstractAuthenticationFailureEvent) {
            status = "FAILURE";
        } else {
            return; // Do not log other types of authentication events
        }

        loginAttemptLogger.log(loginId, currentDateTimeUTC, ipAddress, status);
    }
}
