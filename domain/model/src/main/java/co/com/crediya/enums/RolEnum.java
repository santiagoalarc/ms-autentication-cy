package co.com.crediya.enums;

import java.util.Arrays;

public enum RolEnum {

    ADMIN(0),
        USER(1),
    ASESOR(2);

    private final int id;

    RolEnum(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public static String getName(int id){
        return Arrays.stream(RolEnum.values())
                .filter(rol -> rol.getId() == id)
                .map(Enum::name)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("OBJECT_STATUS_ID_NOT_VALID"));
    }


}
