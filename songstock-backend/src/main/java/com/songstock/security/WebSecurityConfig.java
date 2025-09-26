package com.songstock.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // DESHABILITAMOS CORS EN SECURITY YA QUE LO MANEJAMOS CON EL FILTRO
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Endpoints de Artists
                        .requestMatchers(HttpMethod.GET, "/api/v1/artists/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/artists/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/artists/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/artists/**").hasRole("ADMIN")

                        // Endpoints de Genres
                        .requestMatchers(HttpMethod.GET, "/api/v1/genres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/genres/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/genres/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/genres/**").hasRole("ADMIN")

                        // Endpoints de Albums
                        .requestMatchers(HttpMethod.GET, "/api/v1/albums/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/albums/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/albums/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/albums/**").hasRole("ADMIN")

                        // Endpoints de Products
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasAnyRole("ADMIN", "PROVIDER")

                        // Endpoints sin prefijo (Para compatibilidad)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAnyRole("ADMIN", "PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAnyRole("ADMIN", "PROVIDER")

                        // Otros endpoints existentes
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/providers/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/categories/**").hasAnyRole("ADMIN", "PROVIDER")

                        // Endpoints sin prefijo para ADMIN
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/providers/**").hasRole("ADMIN")
                        .requestMatchers("/categories/**").hasAnyRole("ADMIN", "PROVIDER")

                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}