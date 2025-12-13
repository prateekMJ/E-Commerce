package com.majee.ecommerce.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils) {}
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.debug("AuthTokenFilter : doFilterInternal : AuthTokenFilter called for URI : {}", request.getRequestURI());
        try {
            String jwtToken = parseJwt(request);
            if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.debug("Roles from JWT: {}" , userDetails.getAuthorities());

            }
        }catch (Exception ex){
            logger.error("Cannot set user authentication : {}" , ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String jwtToken = jwtUtils.getJwtFromHeader(request);
        logger.debug("AuthTokenFilter : parseJwt: jwtToken : {}", jwtToken);
        return jwtToken;
    }
}
