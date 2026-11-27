package vn.edu.lwd7071.androidfinalproject.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class ValidatorsTest {
    @Test
    public void checksEmailAndPassword() {
        assertTrue(Validators.isValidEmail(" student+chat@example.edu.vn "));
        assertFalse(Validators.isValidEmail("student.example.edu.vn"));
        assertFalse(Validators.isValidEmail("student @example.edu.vn"));
        assertFalse(Validators.isValidEmail(null));
        assertTrue(Validators.isValidPassword("abc123"));
        assertFalse(Validators.isValidPassword("short"));
    }

    @Test
    public void normalizesVietnamesePhoneNumbersWithoutAcceptingOtherPrefixes() {
        assertEquals("0901234567", Validators.normalizePhone("+84 901 234 567"));
        assertEquals("0901234567", Validators.normalizePhone("0901\u00a0234 567"));
        assertNull(Validators.normalizePhone("+1 901234567"));
        assertNull(Validators.normalizePhone("090123456"));
        assertNull(Validators.normalizePhone("0901x34567"));
    }

    @Test
    public void rejectsBlankGroupNames() {
        assertTrue(Validators.isValidGroupName("Nhóm Android"));
        assertFalse(Validators.isValidGroupName(" \t"));
        assertFalse(Validators.isValidGroupName(null));
    }
}
