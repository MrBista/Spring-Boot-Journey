package com.bisma.foundation.fundamental_spring_boot.repository;

import com.bisma.foundation.fundamental_spring_boot.dto.UserReqDto;
import com.bisma.foundation.fundamental_spring_boot.model.User;

import java.util.List;

public interface UserRepository {
    User saveUser(User user);
    User findUserById(int id);
    List<User> findAllUser();
}
