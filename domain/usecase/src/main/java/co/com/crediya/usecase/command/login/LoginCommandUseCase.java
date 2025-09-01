package co.com.crediya.usecase.command.login;

import co.com.crediya.enums.RolEnum;
import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.LoginUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginCommandUseCase {

    private final UserRepository userRepository;

    public Mono<User> loginUser(LoginUser loginUser){

        return userRepository.login(loginUser)
                .map(user -> user.toBuilder()
                        .rol(RolEnum.getName(user.getIdRol()))
                        .build())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.BAD_CREDENTIALS))));
    }
}
