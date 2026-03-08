package com.bisma.foundation.fundamental_spring_boot.repository;

import com.bisma.foundation.fundamental_spring_boot.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository{
    @Override
    public User saveUser(User user) {

        user.setId(1L);
        return user;
    }

    @Override
    public User findUserById(int id) {
        User user = createuser();
        Long idLong = (long) id;
        user.setId(idLong);

        return user;
    }

    @Override
    public List<User> findAllUser() {
        return List.of();
    }


    private User createuser() {
        User user = new User();
        user.setBirthDate(System.currentTimeMillis());
        user.setEmail("bismen@mail.com");
        user.setPassword("Abc@1234");
        user.setUsername("babayaga");
        user.setName("BabaBisman");
        return user;
    }
}
