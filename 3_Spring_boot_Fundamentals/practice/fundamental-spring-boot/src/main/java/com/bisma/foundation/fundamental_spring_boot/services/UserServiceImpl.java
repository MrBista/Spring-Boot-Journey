package com.bisma.foundation.fundamental_spring_boot.services;

import com.bisma.foundation.fundamental_spring_boot.dto.UserReqDto;
import com.bisma.foundation.fundamental_spring_boot.dto.UserResponseDto;
import com.bisma.foundation.fundamental_spring_boot.mapper.UserMapper;
import com.bisma.foundation.fundamental_spring_boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    private final UserMapper userMapper;


    public UserServiceImpl( UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponseDto> findAllUser() {
        return List.of();
    }

    @Override
    public UserResponseDto findUserById(int id) {
        return userMapper
                .toResponseDTO(userRepository.findUserById(id));
    }

    @Override
    public UserResponseDto createUser(UserReqDto userReqDto) {
        return null;
    }
}
