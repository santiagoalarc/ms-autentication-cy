package co.com.crediya.usecase.command.createuser;

import co.com.crediya.enums.RolEnum;
import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    private final Logger log = Logger.getLogger(CreateUserUseCase.class.getName());

    public Mono<User> saveUser(User user){

        log.log(Level.INFO,"CreateUserUseCase - user {}", user);

        return Mono.just(user)
                .filter(this::validateData)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS))))
                .filter(this::validateBaseSalary)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.INVALID_BASE_SALARY_FORMAT))))
                .flatMap(userData -> userRepository.existsByEmail(userData)
                        .filter(emailExist -> !emailExist)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.EMAIL_ALREADY_REGISTERED))))
                        .thenReturn(userData))
                .map(userData -> userData.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .idRol(RolEnum.USER.getId())
                        .build())
                .flatMap(userRepository::saveUser)
                .doOnError(err -> log.info("ERROR IN - CreateUserUseCase " + err.getMessage()));
    }

    Boolean validateData(User user){

        return notNullAndNotEmpty(user.getName())
                && notNullAndNotEmpty(user.getLastName())
                && notNullAndNotEmpty(user.getEmail())
                && notNullAndNotEmpty(user.getBaseSalary())
                && notNullAndNotEmpty(user.getDocumentIdentification());
    }

    Boolean notNullAndNotEmpty(String value){
        return value != null && !value.isBlank();
    }

    Boolean validateBaseSalary(User user) {
        try {
            double salary = Double.parseDouble(user.getBaseSalary());
            return salary >= 0 && salary <= 15000000;
        } catch (NumberFormatException e) {
            return false;
        }
    }



}
