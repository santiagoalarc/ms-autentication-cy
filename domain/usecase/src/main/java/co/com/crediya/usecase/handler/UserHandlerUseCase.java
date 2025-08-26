package co.com.crediya.usecase.handler;

import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserHandlerUseCase {

    private final UserRepository userRepository;

    public Mono<User> findByDocumentIdentification(String documentIdentification){

        return userRepository.findByDocumentIdentification(documentIdentification)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.DOCUMENT_IDENTIFICATION_NOT_FOUND))));
    }
}
