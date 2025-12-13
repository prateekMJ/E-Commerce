package com.majee.ecommerce.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.jwt.jwtExpirationTimeInMillis}")
    private static int jwtExpirationTimeInMillis;

    @Value("${spring.jwt.jwtSecret}")
    private static String jwtSecret;

    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            logger.debug("getJwtFromHeader: bearerToken={}", bearerToken);
            return bearerToken.substring(7);
        }
        return null;
    }


    public String generateTokenFromUsername(UserDetails userDetails){
        String username =  userDetails.getUsername();
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationTimeInMillis)))
                .signWith(key())
                .compact();
    }


    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }


    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }


    public boolean validateJwtToken(String token){
        try{
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build().parseSignedClaims(token);

            return true;
        }catch(MalformedJwtException e){
            logger.error("Invalid JWT token : {}", e.getMessage());
        }catch(ExpiredJwtException e){
            logger.error("Expired JWT token : {}", e.getMessage());
        }catch(UnsupportedJwtException e){
            logger.error("Unsupported JWT token : {}", e.getMessage());
        }catch(IllegalArgumentException e){
            logger.error("JWT claims string is empty : {}", e.getMessage());
        }

        return false;
    }
}
