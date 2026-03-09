package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{
    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public User create(User user) {
        return null;
    }
}
