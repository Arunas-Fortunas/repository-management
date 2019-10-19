package com.platform_lunar.homework.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class AuthorizationUtils {
    public static final String createEncodedAuthorization(String username, String password) {
        var auth = String.format("%s:%s", username, password);
        var encodedAuth = Base64.encodeBase64(auth.getBytes(US_ASCII));
        return String.format("Basic %s", new String(encodedAuth));
    }
}
