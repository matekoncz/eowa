package com.example.eowa.controller;

import jakarta.servlet.http.Cookie;

public class CookieReader {
    private final Cookie[] cookies;

    public CookieReader(Cookie[] cookies) {
        this.cookies = cookies;
    }

    public boolean hasCookie(String cookieName){
        return getCookie(cookieName) != null;
    }

    public Cookie getCookie(String cookieName){
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
