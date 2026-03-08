package com.bisma.foundation.fundamental_spring_boot.dto;

public class UserReqDto {
    private String username;
    private String name;
    private Long birthDate;
    private int age;

    public UserReqDto() {
    }

    public UserReqDto(String username, String name, Long birthDate, int age) {
        this.username = username;
        this.name = name;
        this.birthDate = birthDate;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserReqDto{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", age=" + age +
                '}';
    }
}
