package co.com.crediya.usecase.handler;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class UsersInfoHandlerUseCase {

    private final UserRepository userRepository;

    public Flux<User> execute(String usersEmail){

        String[] emails= usersEmail.split(",");

        return userRepository.findAllByEmailIn(List.of(emails));

    }
}
