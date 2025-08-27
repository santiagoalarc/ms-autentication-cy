package co.com.crediya.api;

import co.com.crediya.api.config.BaseValidator;
import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.usecase.command.createuser.CreateUserUseCase;
import co.com.crediya.usecase.handler.UserHandlerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateUserUseCase createUserUseCase;
    private final UserHandlerUseCase userHandlerUseCase;
    private final UserDtoMapper userDtoMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateUserDto.class)
                .doOnNext(user -> BaseValidator.validate(user, "PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS"))
                .map(userDtoMapper::toModel)
                .flatMap(createUserUseCase::saveUser)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    public Mono<ServerResponse> listenGetUserByDocId(ServerRequest serverRequest){
        String docId = serverRequest.pathVariable("docId");

        return userHandlerUseCase.findByDocumentIdentification(docId)
                .flatMap(userFound -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userFound));

    }


}
