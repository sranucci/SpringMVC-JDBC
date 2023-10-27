package ar.edu.itba.paw.services.comments;

import ar.edu.itba.paw.dtos.CommentWrapper;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.comments.Comment;
import ar.edu.itba.paw.models.deletion.CommentDeletionData;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.RecipeService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.paw.servicesInterface.comments.CommentsService;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import ar.edu.itba.persistenceInterface.CommentsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsServiceImpl implements CommentsService {

    private final CommentsDao commentsDao;
    private final MailService ms;
    private final UserService us;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentsServiceImpl.class);

    @Autowired
    public CommentsServiceImpl(final CommentsDao cd,final MailService ms,final UserService us) {
        this.commentsDao = cd;
        this.ms = ms;
        this.us = us;
    }

    @Transactional
    @Override
    public boolean deleteComment(long commentId) {
        return commentsDao.deleteComment(commentId);
    }

    public Optional<Comment> createComment(long recipeId, long userId, String commentContent) {
        Optional<Comment> toReturn = commentsDao.createComment(recipeId, userId, commentContent);

        // envio de mails
        if (toReturn.isPresent()) {
            User recipeOwner = us.findByRecipeId(recipeId).orElseThrow(UserNotFoundException::new);

            // si el comentario lo hizo el dueño de la receta, no se le envia un mail
            if (toReturn.get().getUserId() == recipeOwner.getId())
                return toReturn;

            User commenter = us.findById(userId).orElseThrow(UserNotFoundException::new);

            ms.sendNewCommentEmail(recipeOwner.getEmail(), recipeOwner.getName(), commenter.getName(), commentContent, recipeId)
                    .thenAccept(mailStatus -> {
                        if ( ! mailStatus.isSend())
                            LOGGER.error("error sending mail to user with id {} for comment of user with id {} with comment content {} for recipe with id {}. reason\n{}",
                                    recipeOwner.getId(),commenter.getId(),commentContent,recipeId,mailStatus.getError());
                    });
        }

        return toReturn;
    }

    @Override
    @Transactional(readOnly = true)
    public CommentWrapper getComments(long recipeId, Optional<Long> currentUserId, Optional<Long> commentsToBring, Optional<Integer> pageNumber) {
        List<UserComment> commentList;
        if (currentUserId.isPresent()) {
            commentList = commentsDao.getCommentsWithUserInteractionStatus(recipeId, currentUserId.get(), commentsToBring, pageNumber);
        } else {
            commentList = commentsDao.getCommentsById(recipeId, commentsToBring, pageNumber);
        }
        return new CommentWrapper(commentList, commentsDao.getTotalComments(recipeId));
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> getRecipeComment(long commentId) {
        return commentsDao.getRecipeComment(commentId);
    }

    @Transactional
    @Override
    public boolean removeCommentNotifyingUser(long recipeId, long commentId, String deletionMotive) {
        Optional<CommentDeletionData> commentData = commentsDao.getCommentDeletionData(commentId);
        boolean deleted = deleteComment(commentId);
        if (!commentData.isPresent() || !deleted) {
            return false;
        }
        ms.sendCommentDeletionEmail(commentData.get().getUserMail(), commentData.get().getRecipeName(), commentData.get().getCommentData(), deletionMotive)
                .thenAccept(mailStatus -> {
                    if ( ! mailStatus.isSend())
                        LOGGER.error("error sending comment deletion mail for comment with id {} for recipe with id {}.reason\n{}",commentId,recipeId,mailStatus.getError());
                });
        return true;
    }

    @Override
    public long getCountComments(long userId) {
        return commentsDao.getCountComments(userId);
    }
}
