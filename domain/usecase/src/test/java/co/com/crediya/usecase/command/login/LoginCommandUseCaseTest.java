package co.com.crediya.usecase.command.login;

import co.com.crediya.enums.RolEnum;
import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.LoginUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginCommandUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginCommandUseCase loginCommandUseCase;

    private LoginUser loginUser;
    private User user;

    @BeforeEach
    void setUp() {
        loginUser = LoginUser.builder()
                .email("test@crediya.com")
                .password("password123")
                .build();

        user = User.builder()
                .email("test@crediya.com")
                .idRol(1)
                .build();
    }

    @Test
    void loginUser_Success_ReturnsUserWithCorrectRole() {
        when(userRepository.login(any(LoginUser.class))).thenReturn(Mono.just(user));

        StepVerifier.create(loginCommandUseCase.loginUser(loginUser))
                .expectNextMatches(loggedInUser ->
                        loggedInUser.getEmail().equals(user.getEmail()) &&
                                loggedInUser.getRol().equals(RolEnum.getName(user.getIdRol())))
                .verifyComplete();
    }

    @Test
    void loginUser_BadCredentials_ThrowsUserException() {
        when(userRepository.login(any(LoginUser.class))).thenReturn(Mono.empty());

        StepVerifier.create(loginCommandUseCase.loginUser(loginUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.BAD_CREDENTIALS))
                .verify();
    }
}
