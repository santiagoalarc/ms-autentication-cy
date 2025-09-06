package co.com.crediya.usecase.handler;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersInfoHandlerUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UsersInfoHandlerUseCase usersInfoHandlerUseCase;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setEmail("pepito@example.com");
        user1.setName("User One");

        user2 = new User();
        user2.setEmail("maria@example.com");
        user2.setName("User Two");
    }

    @Test
    void execute_shouldReturnUsersWhenEmailsAreProvided() {

        String emailsString = "pepito@example.com,maria@example.com";
        List<String> emailList = List.of("pepito@example.com", "maria@example.com");
        Flux<User> expectedUsers = Flux.just(user1, user2);

        when(userRepository.findAllByEmailIn(emailList)).thenReturn(expectedUsers);

        StepVerifier.create(usersInfoHandlerUseCase.execute(emailsString))
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    void execute_shouldReturnEmptyFluxWhenNoEmailsAreProvided() {
        String emailsString = "";
        List<String> emailList = List.of("");

        when(userRepository.findAllByEmailIn(emailList)).thenReturn(Flux.empty());

        StepVerifier.create(usersInfoHandlerUseCase.execute(emailsString))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void execute_shouldReturnEmptyFluxWhenEmailsStringIsBlank() {

        String emailsString = " ";
        List<String> emailList = List.of(" ");

        when(userRepository.findAllByEmailIn(emailList)).thenReturn(Flux.empty());

        StepVerifier.create(usersInfoHandlerUseCase.execute(emailsString))
                .expectNextCount(0)
                .verifyComplete();
    }
}