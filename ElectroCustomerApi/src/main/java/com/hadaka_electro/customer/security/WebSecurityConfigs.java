package com.hadaka_electro.customer.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigs {

    private CustomerUserDetailsService customerUserDetailsService;

    @Value("${remember.me.key}")
    private String rememberMeKey;

    @Value("${app.frontend.baseUrl}")
    private String frontendBaseUrl;

    public WebSecurityConfigs(CustomerUserDetailsService customerUserDetailsService) {
        this.customerUserDetailsService = customerUserDetailsService;
    }

    // @formatter:off

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//				 this enables cookie-based CSRF so React can read XSRF-TOKEN cookie
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler))
//				.cors(cors -> cors.disable())
//				.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // creates session on login
                )
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers("/auth/**", "/", "/list-categories",
                                        "/products/**", "/general-settings",
                                        "/customers/register", "/customers/save-customer",
                                        "/customers/verify", "/customers/request-password-reset",
                                        "/customers/reset-password","/default_images/**",
                                        "/category_images/**", "/brand_logos/**",
                                        "/product_images/**").permitAll()
                                .anyRequest().authenticated())
                .rememberMe(rememberMe ->
                        rememberMe.rememberMeServices(rememberMeServices()));

        return http.build();
    }

    // @formatter:on

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "https://hadaka-electro-front-end-blond.vercel.app",
                "https://*.vercel.app",
                "http://localhost:4200"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices =
                new TokenBasedRememberMeServices(rememberMeKey, customerUserDetailsService) {
                    @Override
                    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
                        Boolean rememberMeAttr = (Boolean) request.getAttribute("REMEMBER_ME_REQUESTED");
                        return rememberMeAttr != null ? rememberMeAttr : super.rememberMeRequested(request, parameter);
                    }
                };

        rememberMeServices.setTokenValiditySeconds(86400 * 7); // 7 day
        return rememberMeServices;
    }
}
