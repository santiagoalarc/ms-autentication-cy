package co.com.crediya.usecase.handler;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UsersInfoHandlerUseCase {

    private final UserRepository userRepository;

    private final Logger log = Logger.getLogger(UsersInfoHandlerUseCase.class.getName());

    public Flux<User> execute(String usersEmail){

        log.info("ENTER TO UsersInfoHandlerUseCase :: " + usersEmail);

        String[] emails= usersEmail.split(",");

        return userRepository.findAllByEmailIn(List.of(emails));

    }
}
