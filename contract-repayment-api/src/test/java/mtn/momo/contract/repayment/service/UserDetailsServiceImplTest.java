package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.service.impl.UserDetailsServiceImpl;
import mtn.momo.contract.repayment.service.repository.UserRepository;
import mtn.momo.contract.repayment.service.repository.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        String username = "momo.money@mtn.co.za";
        User user = User.builder()
                .username(username)
                .password("password")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String username = "money@mtn.co.zaentuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }
}
