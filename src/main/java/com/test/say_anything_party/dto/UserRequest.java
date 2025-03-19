package com.test.say_anything_party.dto;

import org.springframework.web.multipart.MultipartFile;

//회원가입 dto
public class UserRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String nickname;
    private MultipartFile profile_image;  // 이미지 파일 (선택)

    public UserRequest() {} // 기본 생성자

    public UserRequest(String email, String password, String passwordCheck, String nickname, MultipartFile profile_image) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickname = nickname;
        this.profile_image = profile_image;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPasswordCheck() { return passwordCheck; }
    public String getNickname() { return nickname; }
    public MultipartFile getProfileImage() { return profile_image; }
}
