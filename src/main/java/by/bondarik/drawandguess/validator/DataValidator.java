package by.bondarik.drawandguess.validator;

import java.util.regex.Pattern;

public class DataValidator {
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");

    private static final Pattern LOGIN_PATTERN = Pattern.compile("^[A-Za-z\\d-_]{3,}$");

    private DataValidator() {}

    public static boolean isCorrectLogin(String login) {
        return LOGIN_PATTERN.matcher(login).matches();
    }

    public static boolean isCorrectIpAddress(String ipAddress) {
        return IP_ADDRESS_PATTERN.matcher(ipAddress).matches() || ipAddress.equals("localhost");
    }
}
