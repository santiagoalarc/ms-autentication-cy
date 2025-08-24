package co.com.crediya.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private String id;
    private String name;
    private String lastName;
    private Long birthDate;
    private String email;
    private String address;
    private String documentIdentification;
    private String phoneNumber;
    private String idRol;
    private String baseSalary;
}
