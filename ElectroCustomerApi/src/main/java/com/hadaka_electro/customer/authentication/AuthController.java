package com.hadaka_electro.customer.authentication;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.hadaka_electro.common.entities.Customer;
import com.hadaka_electro.customer.security.CustomerUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final RememberMeServices rememberMeServices;
    private final AuthenticationService authenticationService;


    @Value("${google.client.id}")
    private String googleClientId;

    public AuthController(AuthenticationManager authenticationManager,
                          SecurityContextRepository securityContextRepository,
                          RememberMeServices rememberMeServices,
                          AuthenticationService authenticationService) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.rememberMeServices = rememberMeServices;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public ResponseEntity<CsrfToken> initialReq(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO,
                                                     HttpServletRequest httpRequest,
                                                     HttpServletResponse httpResponse) {

        // 1. Authenticate via standard Spring DaoAuthenticationProvider
        Authentication authResult = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password())
        );

        // 2. Bind authentication to new SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // 3. Persist context to HTTP Session (generates JSESSIONID cookie)
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        // 4. Handle Remember-Me Cookie if frontend requested it
        if (loginRequestDTO.rememberMe()) {
            httpRequest.setAttribute("REMEMBER_ME_REQUESTED", true);
            rememberMeServices.loginSuccess(httpRequest, httpResponse, authResult);
        }

        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authResult.getPrincipal();
        return ResponseEntity.ok(
                Map.of("Email", customerUserDetails.getUsername(), "Role", customerUserDetails.getAuthorities().toString())
        );
    }


    @PostMapping("/google-login")
    public ResponseEntity<Map<String, String>> googleLogin(@RequestBody GoogleLoginRequestDTO dto,
                                                           HttpServletRequest httpRequest,
                                                           HttpServletResponse httpResponse) throws Exception {

        // Verify the Google Token
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(dto.idToken());
        if (idToken == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Google ID token"));
        }


        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        // fetch existing user or create a new one
        Customer customer = authenticationService.fetchOrCreateCustomer(email, firstName, lastName);
        CustomerUserDetails customerUserDetails = new CustomerUserDetails(customer);

        UsernamePasswordAuthenticationToken authResult =
                new UsernamePasswordAuthenticationToken(customerUserDetails, null, customerUserDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return ResponseEntity.ok(
                Map.of("Email", customerUserDetails.getUsername(), "Role", customerUserDetails.getAuthorities().toString())
        );
    }

    @PostMapping("/logout") // Post: to prevent embedded http get logout.
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null) {
            // this invalidates sessions, remember-me cookies and clear the security context
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            // 2. Force response to send "Set-Cookie" headers that erase cookies in client
            new CookieClearingLogoutHandler("remember-me", "JSESSIONID").logout(request, response, authentication);

        }
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}