package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDto;
import co.com.crediya.api.mapper.UserDtoMapper;
import co.com.crediya.usecase.createuser.CreateUserUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    private CreateUserUseCase createUserUseCase;

    private UserDtoMapper userDtoMapper;


    @PostMapping(path = "/usuarios")
    public Mono<ResponseEntity<Void>> commandName(@RequestBody CreateUserDto createUserDto) {


        return createUserUseCase.saveUser(userDtoMapper.toModel(createUserDto))
                        .map(user -> ResponseEntity.status(HttpStatus.CREATED).build());
    }
}
