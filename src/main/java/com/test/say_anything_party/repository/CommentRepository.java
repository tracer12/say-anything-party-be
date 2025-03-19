package com.test.say_anything_party.repository;

import com.test.say_anything_party.model.Comment;
import com.test.say_anything_party.model.Post;
import com.test.say_anything_party.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    void deleteByPost(Post post);

    // 특정 사용자가 작성한 모든 댓글 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);

    // 댓글 작성 시 posts 테이블에 comments 증가
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.comments = p.comments + 1 WHERE p.pid = :pid")
    void incrementCommentCount(Long pid);

    // 댓글 삭제 시 posts 테이블에 comments 감소
    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.comments = p.comments - 1 WHERE p.pid = :pid AND p.comments > 0")
    void decrementCommentCount(Long pid);
}
