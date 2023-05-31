package com.example.sbb.example.preparation.comment;

import com.example.sbb.example.preparation.DataNotFoundException;
import com.example.sbb.example.preparation.answer.Answer;
import com.example.sbb.example.preparation.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getList(Answer answer) {
        return this.commentRepository.findByAnswer(answer);
    }

    public Comment getComment(Integer id) {
        if (id <= 0) return null;
        Optional<Comment> oc = this.commentRepository.findById(id);
        if (oc.isPresent()) {
            return oc.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public List<Comment> getRecentComment() {
        return this.commentRepository.findTop10ByOrderByCreateDateDesc();
    }

    public Page<Comment> getListByUser(int page, SiteUser user) {
        // order by createDate desc;
        List<Sort.Order> sorts = new ArrayList<>(); // 정렬 조건을 저장할 수 있는 Sort.Order 객체로 구성된 List 선언
        sorts.add(Sort.Order.desc("createDate")); // createDate 속성을 내림차순으로 정렬하는 정렬 조건을 List에 저장
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 정렬 조건들이 담긴 List를 넘겨주면 해당 조건에 맞게 정렬 후 해당 페이지를 반환
        return this.commentRepository.findAllByAuthor(user, pageable);
    }

    public void create(Answer answer, Comment parent, String content, SiteUser author) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAnswer(answer);
        comment.setAuthor(author);
        if (parent != null) comment.setParent(parent);
        this.commentRepository.save(comment);
    }

    public void modify(Comment comment, String content) {
        comment.setContent(content);
        comment.setModifyDate(LocalDateTime.now());
        this.commentRepository.save(comment);
    }

    public void delete(Comment comment) {
        this.commentRepository.delete(comment);
    }

    public void vote(Comment comment, SiteUser voter) {
        comment.getVoter().add(voter);
        this.commentRepository.save(comment);
    }
}
