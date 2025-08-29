package com.example.bankcards.util.mappers;

import com.example.bankcards.dto.user.UserDto;
import com.example.bankcards.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
