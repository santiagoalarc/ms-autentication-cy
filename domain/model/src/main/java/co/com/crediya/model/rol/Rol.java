package co.com.crediya.model.rol;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {

    private String id;

    private String rol;
}
