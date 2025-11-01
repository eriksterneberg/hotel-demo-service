package com.hotel.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PiiMaskingConverter.
 */
class PiiMaskingConverterTest {
    
    @Test
    void testMaskEmails_singleEmail() {
        String text = "User email is john.doe@example.com";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("User email is ***@example.com", masked);
    }
    
    @Test
    void testMaskEmails_multipleEmails() {
        String text = "Emails: alice@example.com and bob@test.org";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Emails: ***@example.com and ***@test.org", masked);
    }
    
    @Test
    void testMaskEmails_noEmail() {
        String text = "This text has no email addresses";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals(text, masked);
    }
    
    @Test
    void testMaskEmails_nullValue() {
        String masked = PiiMaskingConverter.maskEmails(null);
        
        assertNull(masked);
    }
    
    @Test
    void testMaskEmails_emptyString() {
        String masked = PiiMaskingConverter.maskEmails("");
        
        assertEquals("", masked);
    }
    
    @Test
    void testMaskEmails_complexEmail() {
        String text = "Contact: user.name+tag@sub.domain.example.com";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Contact: ***@sub.domain.example.com", masked);
    }
    
    @Test
    void testMaskEmails_emailWithNumbers() {
        String text = "User123@test456.com sent a message";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("***@test456.com sent a message", masked);
    }
    
    @Test
    void testMaskEmails_multipleOccurrences() {
        String text = "From test@example.com to test@example.com";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("From ***@example.com to ***@example.com", masked);
    }
    
    @Test
    void testMaskEmails_emailAtStart() {
        String text = "admin@company.com is the administrator";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("***@company.com is the administrator", masked);
    }
    
    @Test
    void testMaskEmails_emailAtEnd() {
        String text = "Contact us at support@help.com";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Contact us at ***@help.com", masked);
    }
    
    @Test
    void testMaskEmails_withSpecialCharacters() {
        String text = "Email: user_name.test+filter@example-domain.co.uk";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Email: ***@example-domain.co.uk", masked);
    }
    
    @Test
    void testMaskEmails_shortDomain() {
        String text = "Short domain: user@ex.io";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Short domain: ***@ex.io", masked);
    }
    
    @Test
    void testMaskEmails_caseInsensitive() {
        String text = "Upper: USER@EXAMPLE.COM and lower: user@example.com";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Upper: ***@EXAMPLE.COM and lower: ***@example.com", masked);
    }
    
    @Test
    void testMaskEmails_withPunctuation() {
        String text = "Email (user@example.com) was found.";
        String masked = PiiMaskingConverter.maskEmails(text);
        
        assertEquals("Email (***@example.com) was found.", masked);
    }
}
