package co.com.crediya.usecase.createuser;

import co.com.crediya.enums.RolEnum;
import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.usecase.command.createuser.CreateUserUseCase;
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
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .baseSalary("5000000")
                .documentIdentification("123456789")
                .build();
    }

    @Test
    void saveUser_Success_ReturnsSavedUser() {
        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user.toBuilder()
                .id("some-uuid")
                .idRol(RolEnum.USER.getId())
                .password(user.getDocumentIdentification())
                .build()));

        StepVerifier.create(createUserUseCase.saveUser(user))
                .expectNextMatches(savedUser ->
                        savedUser.getId() != null &&
                                savedUser.getIdRol().equals(RolEnum.USER.getId()) &&
                                savedUser.getPassword().equals(user.getDocumentIdentification()))
                .verifyComplete();
    }

    @Test
    void saveUser_PayloadWithMissingFields_ThrowsUserException() {
        User incompleteUser = user.toBuilder().name(null).build();

        StepVerifier.create(createUserUseCase.saveUser(incompleteUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS))
                .verify();
    }

    @Test
    void saveUser_InvalidBaseSalary_ThrowsUserException() {
        User invalidSalaryUser = user.toBuilder().baseSalary("invalid_salary").build();

        StepVerifier.create(createUserUseCase.saveUser(invalidSalaryUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.INVALID_BASE_SALARY_FORMAT))
                .verify();
    }

    @Test
    void saveUser_BaseSalaryOutOfRange_ThrowsUserException() {
        User outOfRangeSalaryUser = user.toBuilder().baseSalary("20000000").build();

        StepVerifier.create(createUserUseCase.saveUser(outOfRangeSalaryUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.INVALID_BASE_SALARY_FORMAT))
                .verify();
    }

    @Test
    void saveUser_EmailAlreadyRegistered_ThrowsUserException() {
        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.EMAIL_ALREADY_REGISTERED))
                .verify();
    }

    @Test
    void saveUser_DocumentIdAlreadyRegistered_ThrowsUserException() {
        when(userRepository.existsByEmail(any(User.class))).thenReturn(Mono.just(false));
        when(userRepository.existsByDocumentId(any(User.class))).thenReturn(Mono.just(true));

        StepVerifier.create(createUserUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getError().equals(UserErrorEnum.DOCUMENT_IDENTIFICATION_ALREADY_REGISTERED))
                .verify();
    }
}
