package co.com.crediya.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserDto(
        String id,
        @NotBlank(message = "NAME_CANNOT_BE_EMPTY_OR_NULL")
        String name,
        @NotBlank(message = "LAST_NAME_CANNOT_BE_EMPTY_OR_NULL")
        String lastName,
        Long birthDate,
        @NotBlank(message = "EMAIL_CANNOT_BE_EMPTY_OR_NULL")
        @Email(message = "EMAIL_IS_NOT_VALID")
        String email,
        String address,
        String documentIdentification,
        String phoneNumber,
        Integer idRol,
        @NotBlank(message = "BASE_SALARY_CANNOT_BE_EMPTY_OR_NULL")
        String baseSalary) {
}
