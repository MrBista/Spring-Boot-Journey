package com.bisma.foundation.practice_materi_4_7.services;

import com.bisma.foundation.practice_materi_4_7.dto.UserReqDTO;
import com.bisma.foundation.practice_materi_4_7.dto.UserResponseDTO;
import com.bisma.foundation.practice_materi_4_7.entity.User;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> findAllUsers();
    UserResponseDTO findUserById(Long id);
    UserResponseDTO saveUser(UserReqDTO user);
    void updateUser(UserReqDTO user, Long id);
    void deleteUserById(Long id);
}
