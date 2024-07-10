package mtn.momo.contract.repayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtn.momo.contract.repayment.model.request.LoginRequest;
import mtn.momo.contract.repayment.model.response.LoginResponse;
import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import mtn.momo.contract.repayment.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Operation(summary = "Log in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content =
                    {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = LoginResponse.class))})})
    @PostMapping(value = "/auth/login/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        } catch (BadCredentialsException ex) {
            log.error("Authentication failed for user: {}", loginRequest.getUserName(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }

        final UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getUserName());
        final String jwt = jwtUtil.generateToken(user);
        final String sessionId = UUID.randomUUID().toString();

        return ResponseEntity.ok(new LoginResponse(jwt, sessionId));
    }
}
