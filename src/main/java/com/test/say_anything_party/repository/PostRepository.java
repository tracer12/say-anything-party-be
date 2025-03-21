package com.test.say_anything_party.repository;

import com.test.say_anything_party.model.User;
import com.test.say_anything_party.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreateDateDesc();

    @Query("SELECT p FROM Post p WHERE p.user = :user")
    List<Post> findByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM Post p WHERE p.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.pid = :pid")
    void incrementViews(@Param("pid") Long pid);

    @Query("SELECT p FROM Post p WHERE p.pid = :pid")
    Optional<Post> findPostById(@Param("pid") Long pid);
}