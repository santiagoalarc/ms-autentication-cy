package co.com.crediya.model.user.gateways;


import co.com.crediya.model.user.LoginUser;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {

    Mono<User> saveUser(User user);

    Mono<Boolean> existsByEmail(User user);

    Mono<Boolean> existsByDocumentId(User user);

    Mono<User> findByDocumentIdentification(String documentIdentification);

    Mono<User> login(LoginUser loginUser);

    Flux<User> findAllByEmailIn(List<String> emails);
}
