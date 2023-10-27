package ar.edu.itba.paw.servicesInterface.comments;

import ar.edu.itba.paw.dtos.CommentWrapper;
import ar.edu.itba.paw.models.comments.Comment;

import java.util.Optional;

public interface CommentsService {
    boolean deleteComment(long commentId);

    Optional<Comment> createComment(long recipeId, long userId, String commentContent);
    CommentWrapper getComments(long recipeId, Optional<Long> currentUserId, Optional<Long> commentsToBring, Optional<Integer> pageNumber);
    Optional<Comment> getRecipeComment(long commentId);
    boolean removeCommentNotifyingUser(long recipeId, long commentId, String deletionMotive);
    long getCountComments(long userId);
}
