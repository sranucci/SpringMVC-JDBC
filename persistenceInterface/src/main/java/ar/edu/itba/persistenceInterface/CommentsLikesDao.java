package ar.edu.itba.persistenceInterface;

import ar.edu.itba.paw.models.comments.CommentLikeData;

import java.util.Optional;

public interface CommentsLikesDao {
    boolean modifyCommentUser(long commentId, long userId, boolean liked);

    Optional<CommentLikeData> getCommentUser(long commentId, long userId);

    int createLikeStatus(long commentId, long userId, boolean status);

    long deleteUserLikeEntry(long commentId, long userId);
}
