package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.UserReqDTO;
import com.bisma.foundation.practice_materi_4_7.dto.UserResponseDTO;
import com.bisma.foundation.practice_materi_4_7.entity.User;
import com.bisma.foundation.practice_materi_4_7.exceptions.BadRequestException;
import com.bisma.foundation.practice_materi_4_7.maper.UserMapper;
import com.bisma.foundation.practice_materi_4_7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponseDTO> findAllUsers() {


        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    public UserResponseDTO findUserById(Long id) {


        return userMapper.toResponse(userRepository.findById(id));
    }

    @Override
    public UserResponseDTO saveUser(UserReqDTO user) {

        User userEntity = userMapper.toEntity(user);


        return userMapper
                .toResponse(userRepository.create(userEntity));
    }

    @Override
    public void updateUser(UserReqDTO user, Long id) {

        if (id == null) {
            throw new BadRequestException("Id harus diisi");
        }

        User toUserEntity = userMapper.toEntity(user);

        toUserEntity.setId(id);
        userRepository.update(toUserEntity);
    }

    @Override
    public void deleteUserById(Long id) {

        userRepository.findById(id);

        userRepository.deleteById(id);
    }
}
