package com.test.say_anything_party.repository;

import com.test.say_anything_party.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 이메일로 사용자 찾기

    void deleteByEmail(String email); // 이메일로 사용자 삭제
}
