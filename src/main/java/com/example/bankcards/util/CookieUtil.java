package com.example.bankcards.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (Constants.REFRESH_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void makeRefreshCookie(HttpServletResponse resp, String tokenValue, int maxAgeSec) {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN, tokenValue);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeSec);
        resp.addCookie(cookie);
        resp.setHeader(Constants.COOKIE_SET_COOKIE_HEADER, String.format(Constants.COOKIE_MAKE_HEADERS, tokenValue, maxAgeSec));
    }

    public static void clearRefreshCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie(Constants.REFRESH_TOKEN, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        resp.setHeader(Constants.COOKIE_SET_COOKIE_HEADER, Constants.COOKIE_CLEAR_HEADERS);
    }
}
