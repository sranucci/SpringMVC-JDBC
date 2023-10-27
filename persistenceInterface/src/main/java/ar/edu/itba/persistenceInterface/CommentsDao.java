package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.comments.Comment;
import ar.edu.itba.paw.models.deletion.CommentDeletionData;

import java.util.List;
import java.util.Optional;

public interface CommentsDao {

    Optional<Comment> createComment(long recipeId, long userId, String commentContent);

    boolean deleteComment(long commentId);

    List<UserComment> getCommentsById(long recipeId, Optional<Long> commentsToBring, Optional<Integer> pageNumber);


    Optional<Comment> getRecipeComment(long commentId);


    List<UserComment> getCommentsWithUserInteractionStatus(long recipeId, long userId, Optional<Long> commentsToBring, Optional<Integer> pageNumber);

    long getTotalComments(long recipeId);

    Optional<CommentDeletionData> getCommentDeletionData(long commentId);

    long getCountComments(long userId);

}
