package com.bsep.smart.home.filter;

import com.bsep.smart.home.services.jwt.JwtValidateAndGetUsername;
import com.bsep.smart.home.services.user.GetUserByEmail;
import com.bsep.smart.home.model.Person;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtValidateAndGetUsername jwtValidateAndGetUsername;
    private final GetUserByEmail getUserByEmail;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws
        ServletException, IOException {
        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.substring("Bearer ".length());
        final String username = jwtValidateAndGetUsername.execute(token);

        if (username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final Person user = getUserByEmail.execute(username);
        final List<SimpleGrantedAuthority> authorities = user.getRole().getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.name())).toList();
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, authorities);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
