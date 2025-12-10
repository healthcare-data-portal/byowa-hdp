package dev.byowa.hdp.service;

import dev.byowa.hdp.dto.AuthResponse;
import dev.byowa.hdp.dto.LoginRequest;
import dev.byowa.hdp.dto.RegisterRequest;
import dev.byowa.hdp.model.Role;
import dev.byowa.hdp.model.User;
import dev.byowa.hdp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        // no-op
    }

    @Test
    void register_success_createsUserAndReturnsToken() {
        RegisterRequest req = new RegisterRequest("alice", "pw");
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("hashed");
        when(jwtService.generateToken("alice", Role.PATIENT.name())).thenReturn("token-123");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("token-123", resp.getToken());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("alice", saved.getUsername());
        assertEquals("hashed", saved.getPassword());
        assertEquals(Role.PATIENT, saved.getRole());
    }

    @Test
    void register_invokesJwtWithUsernameAndRole() {
        RegisterRequest req = new RegisterRequest("carol", "secret");
        when(userRepository.existsByUsername("carol")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("enc");
        when(jwtService.generateToken("carol", Role.PATIENT.name())).thenReturn("tkn");

        authService.register(req);

        // ensure user saved and jwt called with correct args
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("carol", Role.PATIENT.name());
    }

    @Test
    void register_duplicateUsername_throws() {
        RegisterRequest req = new RegisterRequest("existing", "pw");
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertTrue(ex.getMessage().toLowerCase().contains("exists"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success_returnsToken() {
        LoginRequest req = new LoginRequest();
        req.setUsername("bob");
        req.setPassword("pw");
        User u = new User();
        u.setUsername("bob");
        u.setPassword("hashed");
        u.setRole(Role.ADMIN);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw", "hashed")).thenReturn(true);
        when(jwtService.generateToken("bob", Role.ADMIN.name())).thenReturn("jwt");

        AuthResponse resp = authService.login(req);
        assertEquals("jwt", resp.getToken());
    }

    @Test
    void login_invokesJwtWithUsernameAndResolvedRole() {
        LoginRequest req = new LoginRequest();
        req.setUsername("dave");
        req.setPassword("pw");
        User u = new User();
        u.setUsername("dave");
        u.setPassword("hashed");
        u.setRole(Role.PATIENT);
        when(userRepository.findByUsername("dave")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("pw", "hashed")).thenReturn(true);

        when(jwtService.generateToken("dave", Role.PATIENT.name())).thenReturn("tok");

        AuthResponse resp = authService.login(req);
        assertEquals("tok", resp.getToken());
        verify(jwtService).generateToken("dave", Role.PATIENT.name());
    }

    @Test
    void login_invalidPassword_throws() {
        LoginRequest req = new LoginRequest();
        req.setUsername("bob");
        req.setPassword("bad");
        User u = new User();
        u.setUsername("bob");
        u.setPassword("hashed");
        u.setRole(Role.PATIENT);
        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("bad", "hashed")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void login_userNotFound_throws() {
        LoginRequest req = new LoginRequest();
        req.setUsername("missing");
        req.setPassword("pw");
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }
}
