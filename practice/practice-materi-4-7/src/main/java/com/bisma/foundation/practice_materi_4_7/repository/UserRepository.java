package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.entity.User;

import java.util.List;

public interface UserRepository {
    User findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    void update(User user);
    User create(User user);
}
