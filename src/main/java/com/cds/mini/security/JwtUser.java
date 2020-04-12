package com.cds.mini.security;

import lombok.Data;

import java.util.List;

@Data
public class JwtUser {
    private String userName;
    private List<String> roles;
}
