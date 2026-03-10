package com.bisma.foundation.practice_materi_4_7.entity;

import java.util.Date;

public class User {
    private Long id;
    private String name;
    private String username;
    private String email;
    private int age;
    private Date birthDate;
    private String password;

    public User() {
    }

    public User(Long id, String name, String username, String email, int age, Date birthDate, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.age = age;
        this.birthDate = birthDate;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", birthDate=" + birthDate +
                ", password='" + password + '\'' +
                '}';
    }
}
