package com.medilexV2.medPlus.security;


import com.medilexV2.medPlus.entity.Medical;
import com.medilexV2.medPlus.repository.MedicalRepository;
import com.medilexV2.medPlus.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;


@Configuration
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserService userService;
    private final MedicalRepository medicalRepository;

    @Autowired
    @Qualifier("handlerExceptionResolver") 
    private HandlerExceptionResolver handlerExceptionResolver;

    public JWTAuthFilter(JWTService jwtService, UserService userService, MedicalRepository medicalRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.medicalRepository = medicalRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String requestTokenHeader = request.getHeader("Authorization");
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = requestTokenHeader.split("Bearer ")[1];
            String email = jwtService.getEmailFromToken(token);
            logger.info("CURRENT LOGIN : " + email);
            //TODO:email should be null or not null
            if (email == null || SecurityContextHolder.getContext().getAuthentication() == null) {
                Medical medical = medicalRepository.findByEmail(email).orElseThrow(
                        () -> new JwtException("User not found with email: " + email)
                );
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(medical, null, medical.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException ex) {
            handlerExceptionResolver.resolveException(request, response, null,
                    new JwtException(ex.getMessage())); // This now includes expired time
        } catch (JwtException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }
}
