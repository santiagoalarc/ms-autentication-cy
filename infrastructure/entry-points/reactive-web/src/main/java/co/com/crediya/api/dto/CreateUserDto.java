package co.com.crediya.api.dto;


public record CreateUserDto(
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
