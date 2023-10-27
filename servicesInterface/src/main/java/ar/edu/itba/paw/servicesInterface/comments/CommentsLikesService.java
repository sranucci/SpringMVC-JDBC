package ar.edu.itba.paw.servicesInterface.comments;

public interface CommentsLikesService {
    void setCommentLikeStatus(long commentId, long userId, boolean likeStatus);
}
