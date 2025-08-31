package co.com.crediya.api.dto;

public record UserResponseDto(
        String id,
        String name,
        String lastName,
        Long birthDate,
        String email,
        String address,
        String documentIdentification,
        String phoneNumber,
        Integer idRol,
        String baseSalary) {

}
