package com.example.demo.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 🚫 1. Ignorar rutas públicas
        if (shouldNotProcess(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🚫 2. Si ya hay usuario autenticado → no hacer nada
        if (alreadyHasUserAuthentication()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔍 3. Obtener header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // ❌ Si no hay Bearer token → continuar
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔐 4. Extraer token
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔥 5. Validar token y obtener claims
       jwtService.parseValidClaims(token).ifPresent(claims -> {
			String username = claims.getSubject();
			if (username == null || username.isBlank()) {
				return;
			}
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (UsernameNotFoundException ignored) {
				// Token válido pero usuario ya no existe: no rellenar el contexto.
			}
		});

		filterChain.doFilter(request, response);
	}

    /**
     * 🚫 Define rutas públicas donde NO se valida JWT
     */
    private boolean shouldNotProcess(HttpServletRequest request) {
        String path = request.getServletPath();

        return path.startsWith("/auth/")
                || path.startsWith("/insumos/")
                || path.startsWith("/h2-console");
    }

    /**
     * 🔐 Verifica si ya hay usuario autenticado
     */
    private boolean alreadyHasUserAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof UserDetails;
    }
}