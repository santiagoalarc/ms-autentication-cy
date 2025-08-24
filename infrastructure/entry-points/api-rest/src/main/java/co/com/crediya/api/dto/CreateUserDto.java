package co.com.crediya.api.dto;

public record CreateUserDto(
         String name,
         String lastName,
         Long birthDate,
         String email,
         String address,
         String documentIdentification,
         String phoneNumber,
         String idRol,
         String baseSalary) {
}
