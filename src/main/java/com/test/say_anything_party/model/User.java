package com.test.say_anything_party.model;

import jakarta.persistence.*;

//User 엔티티임을 선언하는 @Entity
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image") // 명시적으로 데이터베이스의 profile_image와 매핑
    private String profileImage;
    
    //jpa를 위해 필요한 기본 생성자
    protected User() {}

    //사용자 정보를 설정하는 생성자
    public User(String email, String password, String nickname, String profileImage) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    //필드값 읽어오는 메서드
    public Long getUid() { return uid; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public String getProfileImage() { return profileImage; }

    // 닉네임 변경
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // 프로필 이미지 변경
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    // 비밀번호 변경 (추가)
    public void setPassword(String password) {
        this.password = password;
    }
}
