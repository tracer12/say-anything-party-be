package com.test.say_anything_party.dto;

// 비밀번호 변경 dto
public class PasswordRequest {
    private String password;
    private String passwordCheck;

    public String getPassword() {
        return password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }
}
