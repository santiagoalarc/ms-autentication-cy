package co.com.crediya.enums;

public enum RolEnum {

    ADMIN(0),
        USER(1);

    private final int id;

    RolEnum(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }


}
