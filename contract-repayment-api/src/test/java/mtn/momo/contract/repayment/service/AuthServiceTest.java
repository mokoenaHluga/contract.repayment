package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.exception.UserAlreadyExistException;
import mtn.momo.contract.repayment.model.dto.UserDto;
import mtn.momo.contract.repayment.service.impl.AuthServiceI;
import mtn.momo.contract.repayment.service.repository.UserRepository;
import mtn.momo.contract.repayment.service.repository.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceI authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterNewUser_Success() throws UserAlreadyExistException {
        UserDto userDto = UserDto.builder()
                .name("Hloni")
                .surname("Mokoena")
                .username("Hloni.mtn@momo.co.za")
                .password("password")
                .build();

        User newUser = User.builder()
                .name("Hloni")
                .surname("Mokoena")
                .username("Hloni.mtn@momo.co.za")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("Hloni.mtn@momo.co.za")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDto registeredUser = authService.registerNewUser(userDto);

        assertNotNull(registeredUser);
        assertEquals("Hloni.mtn@momo.co.za", registeredUser.getUsername());
        assertEquals("Hloni", registeredUser.getName());
        assertEquals("Mokoena", registeredUser.getSurname());

        verify(userRepository, times(1)).findByUsername("Hloni.mtn@momo.co.za");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterNewUser_UserAlreadyExists() {
        UserDto userDto = UserDto.builder()
                .name("Hloni")
                .surname("Mokoena")
                .username("Hloni.mtn@momo.co.za")
                .password("password")
                .build();

        User existingUser = User.builder()
                .name("Hloni")
                .surname("Mokoena")
                .username("Hloni.mtn@momo.co.za")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername("Hloni.mtn@momo.co.za")).thenReturn(Optional.of(existingUser));

        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> {
            authService.registerNewUser(userDto);
        });

        assertEquals("User name already exists", exception.getMessage());

        verify(userRepository, times(1)).findByUsername("Hloni.mtn@momo.co.za");
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }
}
