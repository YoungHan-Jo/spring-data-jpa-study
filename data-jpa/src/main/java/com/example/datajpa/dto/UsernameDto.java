package com.example.datajpa.dto;

import lombok.Getter;

@Getter
public class UsernameDto {

    private final String username;
    private final int age;

    public UsernameDto(String username, int age) {
        this.username = username;
        this.age = age;
    }

}
