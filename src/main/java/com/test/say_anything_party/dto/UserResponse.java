package com.test.say_anything_party.dto;

// 유저 정보 받아오는 dto
public class UserResponse {
    private Long uid;
    private String email;
    private String nickname;
    private String profile_image; // ✅ Java에서도 profileImage 유지

    public UserResponse(Long uid, String email, String nickname, String profile_image) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.profile_image = profile_image;
    }

    public Long getUid() { return uid; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
    public String getProfileImage() { return profile_image; } // ✅ profileImage 유지
}
