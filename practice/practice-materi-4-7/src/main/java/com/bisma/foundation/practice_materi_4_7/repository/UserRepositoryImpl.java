package com.bisma.foundation.practice_materi_4_7.repository;

import com.bisma.foundation.practice_materi_4_7.entity.User;
import com.bisma.foundation.practice_materi_4_7.exceptions.BadRequestException;
import com.bisma.foundation.practice_materi_4_7.exceptions.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository{
    // kedepannya di impl repository itu akan query by db

    private List<User> users = new ArrayList<>();

    public UserRepositoryImpl() {
        this.users.addAll(generateUsers(20));
    }


    private static User user(int index) {
        User user = new User();
        // Menggunakan index agar email, username, dan id berbeda tiap iterasi
        user.setEmail("user" + index + "@mail.com");
        user.setAge(20 + index); // Umur akan bervariasi dari 20, 21, dst.
        user.setUsername("babayaga_" + index);
        user.setName("bobanaganame_" + index);
        user.setPassword("password" + index);
        user.setId((long) index + 1); // ID urut dari 1, 2, 3...
        return user;
    }

    private static List<User> generateUsers(int amountUsers) {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < amountUsers; i++) {
            // Oper nilai i ke method user()
            userList.add(user(i));
        }
        return userList;
    }


    @Override
    public User findById(Long id) {
        validateUserId(id);

        return users
                .stream()
                .filter(val -> val.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Override
    public List<User> findAll() {

        return users;
    }

    @Override
    public void deleteById(Long id) {
        validateUserId(id);
         users = users
                 .stream()
                 .filter(user -> !user.getId().equals(id))
                 .toList();

    }

    @Override
    public void update(User user) {
        users = users.stream().map((val) -> {
            if (val.getId().equals(user.getId())) {
                return user;
            }
            return val;
        }).toList();
    }

    @Override
    public User create(User user) {
        User lastUser = users.get(users.size() - 1);
        user.setId(lastUser.getId());
        users.add(user);
        return user;
    }


    private void validateUserId(Long id) {
        if (id == null) {
            throw new BadRequestException("Id harus diisi");
        }
    }
}
