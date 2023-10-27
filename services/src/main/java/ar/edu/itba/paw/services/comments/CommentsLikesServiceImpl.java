package ar.edu.itba.paw.services.comments;

import ar.edu.itba.paw.models.comments.CommentLikeData;
import ar.edu.itba.paw.servicesInterface.comments.CommentsLikesService;
import ar.edu.itba.persistenceInterface.CommentsLikesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentsLikesServiceImpl implements CommentsLikesService {
    private final CommentsLikesDao commentsLikesDao;

    @Autowired
    public CommentsLikesServiceImpl(final CommentsLikesDao commentsLikesDao) {
        this.commentsLikesDao = commentsLikesDao;
    }


    @Transactional
    @Override
    public void setCommentLikeStatus(long commentId, long userId, boolean likeStatus) {
        Optional<CommentLikeData> data = commentsLikesDao.getCommentUser(commentId, userId);
        if (data.isPresent()) {
            CommentLikeData commentData = data.get();
            if (likeStatus != commentData.isLiked())
                commentsLikesDao.modifyCommentUser(commentId, userId, likeStatus);
            else
                commentsLikesDao.deleteUserLikeEntry(commentId, userId);
            return;
        }
        commentsLikesDao.createLikeStatus(commentId, userId, likeStatus);
    }
}
