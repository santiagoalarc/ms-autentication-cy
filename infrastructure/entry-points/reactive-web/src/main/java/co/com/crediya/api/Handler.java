package co.com.crediya.api;

import co.com.crediya.api.config.BaseValidator;
import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.dto.LoginReqDto;
import co.com.crediya.api.mapper.LoginDtoMapper;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.api.security.JwtProvider;
import co.com.crediya.model.user.TokenDto;
import co.com.crediya.usecase.command.createuser.CreateUserUseCase;
import co.com.crediya.usecase.command.login.LoginCommandUseCase;
import co.com.crediya.usecase.handler.UserHandlerUseCase;
import co.com.crediya.usecase.handler.UsersInfoHandlerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final CreateUserUseCase createUserUseCase;
    private final UserHandlerUseCase userHandlerUseCase;
    private final LoginCommandUseCase loginCommandUseCase;
    private final UsersInfoHandlerUseCase usersInfoHandlerUseCase;
    private final UserDtoMapper userDtoMapper;
    private final LoginDtoMapper loginDtoMapper;
    private final JwtProvider jwtProvider;

    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateUserDto.class)
                .doOnNext(user -> BaseValidator.validate(user, "PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS"))
                .map(userDtoMapper::toModel)
                .flatMap(createUserUseCase::saveUser)
                .map(userDtoMapper::toResponse)
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .onErrorResume(org.springframework.security.access.AccessDeniedException.class,
                        ex -> ServerResponse.status(HttpStatus.FORBIDDEN)
                                .bodyValue("Acceso denegado: Se requiere rol ADMIN para crear usuarios"));
    }

    @PreAuthorize("hasAuthority('USER')")
    public Mono<ServerResponse> listenGetUserByDocId(ServerRequest serverRequest){
        String docId = serverRequest.pathVariable("docId");

        return userHandlerUseCase.findByDocumentIdentification(docId)
                .map(userDtoMapper::toUserInfoDTO)
                .flatMap(userFound -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userFound));

    }

    public Mono<ServerResponse> loginUser(ServerRequest serverRequest){

        return serverRequest.bodyToMono(LoginReqDto.class)
                .map(loginDtoMapper::toModel)
                .flatMap(loginCommandUseCase::loginUser)
                .map(user -> new TokenDto(jwtProvider.generateToken(user)))
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    @PreAuthorize("hasAnyAuthority('ASESOR', 'USER')")
    public Mono<ServerResponse> getUserByEmail(ServerRequest serverRequest){
        String emails = serverRequest.queryParam("emails").orElse("");

        return usersInfoHandlerUseCase.execute(emails)
                .map(userDtoMapper::toUserInfoDTO)
                .collectList()
                .flatMap(usersFound -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(usersFound));
    }

    public Mono<ServerResponse> healthCheck(ServerRequest serverRequest){
        return ServerResponse.ok().bodyValue("ok");
    }


}
