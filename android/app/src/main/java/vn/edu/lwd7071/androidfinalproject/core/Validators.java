package vn.edu.lwd7071.androidfinalproject.core;

import java.util.regex.Pattern;

public final class Validators {
    private static final Pattern EMAIL = Pattern.compile(
            "[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+");
    private static final Pattern PHONE = Pattern.compile("0[0-9]{9}");

    private Validators() {}

    public static boolean isValidEmail(String email) {
        return email != null && email.trim().length() <= 254 && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String normalized = phone.replaceAll("[\\s\\p{Zs}]", "");
        if (normalized.startsWith("+84")) {
            normalized = "0" + normalized.substring(3);
        }
        return PHONE.matcher(normalized).matches() ? normalized : null;
    }

    public static boolean isValidGroupName(String title) {
        return title != null && !title.trim().isEmpty();
    }
}
