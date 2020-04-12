package com.cds.mini.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtUserDetails extends User {
    public JwtUserDetails(String username, Collection<? extends GrantedAuthority> authorities) {
        super(username, "***", authorities);
    }
}
