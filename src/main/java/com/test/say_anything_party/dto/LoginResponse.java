package com.test.say_anything_party.dto;

//로그인 응답 dto, accessToken, refreshToken, 이미지 포함
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String profileImage;

    // 생성자 추가
    public LoginResponse(String accessToken, String refreshToken, String profileImage) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImage = profileImage;
    }

    // Getter 추가
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getProfileImage() { return profileImage; }
}
