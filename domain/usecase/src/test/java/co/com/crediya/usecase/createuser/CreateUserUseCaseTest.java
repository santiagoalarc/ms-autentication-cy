package co.com.crediya.usecase.createuser;

import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.createuser.CreateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .baseSalary("1500000")
                .documentIdentification("123456789")
                .build();
    }

    @Test
    @DisplayName("Should throw an exception when required fields are missing")
    void saveUser_missingRequiredFields_throwsException() {
        User invalidUser = validUser.toBuilder().name(null).build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS)
                .verify();
    }

    @Test
    @DisplayName("Should throw an exception for invalid email format")
    void saveUser_invalidEmailFormat_throwsException() {
        User invalidUser = validUser.toBuilder().email("invalid-email").build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.INVALID_EMAIL_FORMAT)
                .verify();
    }

    @Test
    @DisplayName("Should throw an exception when the base salary is not numeric")
    void saveUser_baseSalaryNotNumeric_throwsException() {
        User invalidUser = validUser.toBuilder().baseSalary("not-a-number").build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.INVALID_BASE_SALARY_FORMAT)
                .verify();
    }

    @Test
    @DisplayName("Should throw an exception when the base salary is out of range")
    void saveUser_baseSalaryOutOfRange_throwsException() {
        User invalidUser = validUser.toBuilder().baseSalary("20000000").build();

        StepVerifier.create(createUserUseCase.saveUser(invalidUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.INVALID_BASE_SALARY_FORMAT)
                .verify();
    }

    @Test
    @DisplayName("Should throw an exception when the email is already registered")
    void saveUser_emailAlreadyExists_throwsException() {
        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.saveUser(validUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.EMAIL_ALREADY_REGISTERED)
                .verify();
    }

}
