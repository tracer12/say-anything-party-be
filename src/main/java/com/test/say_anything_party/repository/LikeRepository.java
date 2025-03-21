package com.test.say_anything_party.repository;

import com.test.say_anything_party.model.Like;
import com.test.say_anything_party.model.Post;
import com.test.say_anything_party.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // ✅ 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserAndPost(User user, Post post);

    // ✅ 특정 사용자의 특정 게시글 좋아요 조회
    Optional<Like> findByUserAndPost(User user, Post post);

    // ✅ 특정 게시글에 대한 모든 좋아요 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.post = :post")
    void deleteByPost(@Param("post") Post post);

    boolean existsByPost(Post post);

    @Modifying
    @Transactional
    @Query("DELETE FROM Like l WHERE l.user = :user")
    void deleteByUser(@Param("user") User user);
}
