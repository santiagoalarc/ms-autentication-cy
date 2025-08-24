package co.com.crediya.jpa.user;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_user")
public class UserEntity implements Serializable {

    @Id
    private String id;
    private String name;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    private Long birthDate;
    private String email;
    private String address;
    @Column(name = "document_identification")
    private String documentIdentification;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "id_rol")
    private String idRol;
    @Column(name = "base_salary")
    private String baseSalary;
}
