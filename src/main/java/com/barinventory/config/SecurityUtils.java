package com.barinventory.config;

import org.springframework.security.core.context.SecurityContextHolder;

import com.barinventory.services.CustomUserDetails;

public class SecurityUtils {

    public static Long getBarId() {

        return ((CustomUserDetails)
                SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
        ).getBarId();
    }
}