package co.com.crediya.usecase.createuser;

import co.com.crediya.enums.UserErrorEnum;
import co.com.crediya.exceptions.UserException;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//El sistema debe validar que el correo_electronico proporcionado no esté previamente registrado por otro solicitante.
//El sistema debe validar el formato correcto de los datos, como por ejemplo, que el salario_base sea un valor numérico (este entre 0 y 15_000_000) y el correo_electronico tenga un formato de email válido.
//Una vez registrado exitosamente, la información del solicitante debe quedar guardada permanentemente en la base de datos.

@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public Mono<User> saveUser(User user){

        return Mono.just(user)
                .filter(this::validateData)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.PAYLOAD_NOT_CONTAIN_MINIMUM_FIELDS))))
                .filter(this::validateEmail)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.INVALID_EMAIL_FORMAT))))
                .filter(this::validateBaseSalary)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.INVALID_BASE_SALARY_FORMAT))))
                .flatMap(userData -> userRepository.existsByEmail(userData)
                        .filter(emailExist -> !emailExist)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new UserException(UserErrorEnum.EMAIL_ALREADY_REGISTERED))))
                        .thenReturn(userData))
                .map(userData -> userData.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .build())
                .flatMap(userRepository::saveUser);
    }

    Boolean validateData(User user){

        return notNullAndNotEmpty(user.getName())
                && notNullAndNotEmpty(user.getLastName())
                && notNullAndNotEmpty(user.getEmail())
                && notNullAndNotEmpty(user.getBaseSalary());
    }

    Boolean validateEmail(User user){

            Matcher matcher = EMAIL_PATTERN.matcher(user.getEmail());
            return matcher.matches();
    }

    Boolean notNullAndNotEmpty(String value){
        return value != null && !value.isBlank();
    }

    Boolean validateBaseSalary(User user) {
        try {
            double salary = Double.parseDouble(user.getBaseSalary());
            if (salary >= 0 && salary <= 15000000) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }



}
