package mtn.momo.contract.repayment.controller;

import mtn.momo.contract.repayment.model.request.LoginRequest;
import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import mtn.momo.contract.repayment.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    private MockMvc mockMvc;

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJzdWIiOiJtdG4ubW9tb0BtdG4uY28uemEiLCJpYXQiOjE3MjAyNjM0NjMsImV4cCI6MTcyMDM0OTg2M30" +
            ".tjHQnpoa5RRhZNrq8ooEz10BeVQlXtS23cIEuyHcN84";

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authenticationManager, jwtUtil, userDetailsService))
                .build();
    }

    @Test
    public void testAuthenticate_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName("mtn.momo@mtn.co.za");
        loginRequest.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(loginRequest.getUserName())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(VALID_TOKEN);

        mockMvc.perform(post("/api/auth/login/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"mtn.momo@mtn.co.za\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(VALID_TOKEN));
    }

    @Test
    public void testAuthenticate_Failure() throws Exception {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Incorrect username or password"));

        mockMvc.perform(post("/api/auth/login/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userName\":\"mtn.momo@mtn.co.za\", \"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }
}

