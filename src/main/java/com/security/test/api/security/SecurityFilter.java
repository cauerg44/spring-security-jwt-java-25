package com.security.test.api.security;

import com.security.test.api.repository.UserRepository;
import com.security.test.api.utils.TokenProviderUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = Logger.getLogger(SecurityFilter.class.getName());
    private final TokenProviderUtil tokenProvider;
    private final UserRepository userRepository;

    public SecurityFilter(TokenProviderUtil tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);
        
        if (token == null || token.isEmpty()) {
            logger.info("Token não encontrado no header Authorization");
            filterChain.doFilter(request, response);
            return;
        }
        
        authenticateUser(token);
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) {
        var login = tokenProvider.validateToken(token);
        logger.info("Token validado. Login: " + login);
        
        if (login == null || login.isEmpty()) {
            logger.warning("Token inválido ou login vazio");
            return;
        }
        
        UserDetails user = userRepository.findByLogin(login);
        logger.info("Usuário encontrado: " + (user != null ? user.getUsername() : "null"));
        
        if (user == null) {
            logger.warning("Usuário não encontrado no banco de dados para login: " + login);
            return;
        }
        
        logger.info("Authorities: " + user.getAuthorities());

        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Usuário autenticado no SecurityContext");
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        if (!authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "").trim();
    }
}