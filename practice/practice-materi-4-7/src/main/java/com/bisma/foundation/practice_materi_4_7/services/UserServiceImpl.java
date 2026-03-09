package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.UserResponseDTO;
import com.bisma.foundation.practice_materi_4_7.entity.User;
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
        return List.of();
    }

    @Override
    public UserResponseDTO findUserById(Long id) {
        return null;
    }

    @Override
    public UserResponseDTO saveUser(User user) {
        return null;
    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public void deleteUserById(Long id) {

    }
}
