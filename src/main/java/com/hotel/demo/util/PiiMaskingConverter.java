package com.hotel.demo.util;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logback converter that masks PII (Personally Identifiable Information) in log messages.
 * Specifically masks email addresses to prevent them from appearing in logs.
 */
public class PiiMaskingConverter extends ClassicConverter {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        if (message == null) {
            return null;
        }
        return maskEmails(message);
    }
    
    /**
     * Masks email addresses in the given text.
     * Example: john.doe@example.com becomes ***@example.com
     */
    public static String maskEmails(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        StringBuffer masked = new StringBuffer();
        
        while (matcher.find()) {
            String email = matcher.group();
            String maskedEmail = maskEmail(email);
            matcher.appendReplacement(masked, Matcher.quoteReplacement(maskedEmail));
        }
        matcher.appendTail(masked);
        
        return masked.toString();
    }
    
    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return "***" + email.substring(atIndex);
        }
        return "***";
    }
}
