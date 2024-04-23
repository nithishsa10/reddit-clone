package dev.clone.reddit.service;

import dev.clone.reddit.dto.CommentsDto;
import dev.clone.reddit.exception.PostNotFoundException;
import dev.clone.reddit.mapper.CommentMapper;
import dev.clone.reddit.model.Comment;
import dev.clone.reddit.model.NotificationEmail;
import dev.clone.reddit.model.Post;
import dev.clone.reddit.model.User;
import dev.clone.reddit.repository.CommentRepository;
import dev.clone.reddit.repository.PostRepository;
import dev.clone.reddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;


    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post." + commentsDto.getText());
        sendCommentNotification(message, post.getUser());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return  commentRepository.findByPost(post)
                         .stream()
                         .map(commentMapper::mapToDto)
                         .collect(toList());
    }

    public List<CommentsDto> getAllCommentsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return  commentRepository.findByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }
}
