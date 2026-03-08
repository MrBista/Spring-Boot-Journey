package com.bisma.foundation.fundamental_spring_boot.dto;

public class UserResponseDto {
    private String name;
    private String email;
    private int age;
    private Long birthDate;

    public UserResponseDto() {
    }

    public UserResponseDto(String name, String email, int age, Long birthDate) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.birthDate = birthDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", birthData=" + birthDate +
                '}';
    }
}
