package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginAttemptLogger {

    private static final String LOG_FILE_NAME = "login_attempts.csv";
    private static final String CSV_HEADER = "Login ID,Timestamp (UTC),IP Address,Status";

    public synchronized void log(String loginId, String dateTime, String ipAddress, String status) {
        File logFile = new File(LOG_FILE_NAME);
        boolean writeHeader = !logFile.exists();

        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            if (writeHeader) {
                out.println(CSV_HEADER);
            }

            String logEntry = String.format("\"%s\",\"%s\",\"%s\",\"%s\"",
                    loginId, dateTime, ipAddress, status);
            out.println(logEntry);

        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
            // Alternatively, if slf4j is available:
            // import org.slf4j.Logger;
            // import org.slf4j.LoggerFactory;
            // private static final Logger logger = LoggerFactory.getLogger(LoginAttemptLogger.class);
            // logger.error("Error writing to log file: {}", e.getMessage(), e);
        }
    }
}
