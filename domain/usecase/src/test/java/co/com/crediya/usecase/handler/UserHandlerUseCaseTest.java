package co.com.crediya.usecase.handler;

import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserHandlerUseCase userHandlerUseCase;

    private User testUser;
    private static final String DOCUMENT_ID = "123456789";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .documentIdentification(DOCUMENT_ID)
                .name("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @DisplayName("Should return a User when the document exists")
    void findByDocumentIdentification_userFound_returnsUser() {

        when(userRepository.findByDocumentIdentification(DOCUMENT_ID))
                .thenReturn(Mono.just(testUser));

        StepVerifier.create(userHandlerUseCase.findByDocumentIdentification(DOCUMENT_ID))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw an exception when the document does not exist")
    void findByDocumentIdentification_userNotFound_throwsException() {
       
        when(userRepository.findByDocumentIdentification(DOCUMENT_ID))
                .thenReturn(Mono.empty());


        StepVerifier.create(userHandlerUseCase.findByDocumentIdentification(DOCUMENT_ID))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserException &&
                                ((UserException) throwable).getErrorEnum() == UserErrorEnum.DOCUMENT_IDENTIFICATION_NOT_FOUND
                )
                .verify();
    }
}