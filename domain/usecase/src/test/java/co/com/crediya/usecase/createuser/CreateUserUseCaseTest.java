package co.com.crediya.usecase.createuser;

import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary("1500000")
                .build();
    }

    @Test
    void shouldSaveUserSuccessfully() {

        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(validUser));

        StepVerifier.create(createUserUseCase.saveUser(validUser))
                .expectNextMatches(user -> user.getName().equals("John"))
                .verifyComplete();
    }

    @Test
    void shouldThrowErrorWhenRequiredFieldsAreMissing() {

        User invalidUser = User.builder()
                .name(null)
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary("1500000")
                .build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError() == UserErrorEnum.PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS)
                .verify();
    }

    @Test
    void shouldThrowErrorWhenEmailFormatIsInvalid() {

        User invalidUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("invalid-email")
                .baseSalary("1500000")
                .build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError() == UserErrorEnum.INVALID_EMAIL_FORMAT)
                .verify();
    }

    @Test
    void shouldThrowErrorWhenBaseSalaryIsNotNumeric() {

        User invalidUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary("not-a-number")
                .build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError() == UserErrorEnum.INVALID_BASE_SALARY_FORMAT)
                .verify();
    }

    @Test
    void shouldThrowErrorWhenBaseSalaryIsOutOfRange() {

        User invalidUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary("20000000")
                .build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError() == UserErrorEnum.INVALID_BASE_SALARY_FORMAT)
                .verify();
    }

    @Test
    void shouldThrowErrorWhenEmailAlreadyExists() {

        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.saveUser(validUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError() == UserErrorEnum.EMAIL_ALREADY_REGISTERED)
                .verify();
    }
}