package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoginReqDto;
import co.com.crediya.model.user.LoginUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginDtoMapper {

    LoginUser toModel(LoginReqDto loginReqDto);
}
