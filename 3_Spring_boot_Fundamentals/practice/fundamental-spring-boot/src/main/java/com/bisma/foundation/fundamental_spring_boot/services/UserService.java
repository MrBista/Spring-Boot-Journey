package com.bisma.foundation.fundamental_spring_boot.services;

import com.bisma.foundation.fundamental_spring_boot.dto.UserReqDto;
import com.bisma.foundation.fundamental_spring_boot.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> findAllUser();
    UserResponseDto findUserById(int id);
    UserResponseDto createUser(UserReqDto userReqDto);
}
