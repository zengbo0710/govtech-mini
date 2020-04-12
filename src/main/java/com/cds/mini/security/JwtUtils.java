package com.cds.mini.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Arrays;

public class JwtUtils {
    private static final String SECRET = "W3lcome!";

    public static String generate(JwtUser jwtUser) {

        Claims claims = Jwts.claims()
                .setSubject(jwtUser.getUserName());
        claims.put("roles", jwtUser.getRoles());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Claims decode(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public static void main(String[] args) {
        JwtUser user = new JwtUser();
        user.setUserName("admin");
        user.setRoles(Arrays.asList("ADMIN", "PM"));

        String token = generate(user);
        Claims claims = decode(token);
        System.out.println(claims);
    }
}
