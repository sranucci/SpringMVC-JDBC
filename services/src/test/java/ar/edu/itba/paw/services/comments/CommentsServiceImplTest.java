package ar.edu.itba.paw.services.comments;

import ar.edu.itba.paw.dtos.CommentWrapper;
import ar.edu.itba.paw.enums.UserInteraction;
import ar.edu.itba.paw.mailstatus.MailStatus;
import ar.edu.itba.paw.models.UserComment;
import ar.edu.itba.paw.models.deletion.CommentDeletionData;
import ar.edu.itba.paw.servicesInterface.MailService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.persistenceInterface.CommentsDao;
import ar.edu.itba.persistenceInterface.CommentsLikesDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)

public class CommentsServiceImplTest {

    private static final long COMMENT_ID = 1;
    private static final long RECIPE_ID = 1;
    private static final long USER_ID = 1;
    private static final boolean LIKED = true;
    private static  final String USER_NAME = "name";
    private static final String USER_LASTNAME = "last name";
    private static final String COMMENT = "comment";
    private static final Instant CREATED_AT = Instant.now();
    private static final long DISLIKES = 1;
    private static final long LIKES = 1;
    private static final UserInteraction USER_INTERACTION = UserInteraction.LIKE;

    private static final String USER_EMAIL = "email";
    private static final String RECIPE_NAME = "recipe";
    private static final String DELETION_MOTIVE= "deletion";
    private static final String SUBJECT = "subject";

    @Mock
    private CommentsDao commentsDao;
    @Mock
    private MailService ms;


    @InjectMocks
    private CommentsServiceImpl cs;

    @Test
    public void testGetCommentsWhenUserIsPresent(){
        List<UserComment> commentList = new ArrayList<>();
        UserComment comment = new UserComment(
                COMMENT_ID, USER_NAME, USER_LASTNAME, COMMENT, CREATED_AT, USER_ID, DISLIKES, LIKES, USER_INTERACTION);
        commentList.add(comment);

        when(commentsDao.getCommentsWithUserInteractionStatus(RECIPE_ID, USER_ID,Optional.of(1L),Optional.of(1))).thenReturn(commentList);
        when(commentsDao.getTotalComments(RECIPE_ID)).thenReturn(1L);

        CommentWrapper commentWrapper = cs.getComments(RECIPE_ID, Optional.of(USER_ID), Optional.of(1L), Optional.of(1));
        assertEquals(1, commentWrapper.getCommentList().size());
        assertEquals(1, commentWrapper.getTotalComments());
        assertEquals(COMMENT_ID, commentWrapper.getCommentList().get(0).getCommentId());
        assertEquals(USER_NAME, commentWrapper.getCommentList().get(0).getUserName());
        assertEquals(USER_LASTNAME, commentWrapper.getCommentList().get(0).getUserLastName());
        assertEquals(COMMENT, commentWrapper.getCommentList().get(0).getComment());
        assertEquals(Date.from(CREATED_AT), commentWrapper.getCommentList().get(0).getCreatedAt());
        assertEquals(DISLIKES, commentWrapper.getCommentList().get(0).getDislikes());
        assertEquals(LIKES, commentWrapper.getCommentList().get(0).getLikes());
        assertEquals(USER_INTERACTION, commentWrapper.getCommentList().get(0).getUserInteraction());

    }

    @Test
    public void testGetCommentsWhenUserIsNotPresent(){
        List<UserComment> commentList = new ArrayList<>();
        UserComment comment = new UserComment(
                COMMENT_ID, USER_NAME, USER_LASTNAME, COMMENT, CREATED_AT, USER_ID, DISLIKES, LIKES, UserInteraction.NO_INTERACTION);
        commentList.add(comment);

        when(commentsDao.getCommentsById(RECIPE_ID, Optional.of(1L),Optional.of(1))).thenReturn(commentList);
        when(commentsDao.getTotalComments(RECIPE_ID)).thenReturn(1L);

        CommentWrapper commentWrapper = cs.getComments(RECIPE_ID, Optional.empty(), Optional.of(1L), Optional.of(1));
        assertEquals(1, commentWrapper.getCommentList().size());
        assertEquals(1, commentWrapper.getTotalComments());
        assertEquals(COMMENT_ID, commentWrapper.getCommentList().get(0).getCommentId());
        assertEquals(USER_NAME, commentWrapper.getCommentList().get(0).getUserName());
        assertEquals(USER_LASTNAME, commentWrapper.getCommentList().get(0).getUserLastName());
        assertEquals(COMMENT, commentWrapper.getCommentList().get(0).getComment());
        assertEquals(Date.from(CREATED_AT), commentWrapper.getCommentList().get(0).getCreatedAt());
        assertEquals(DISLIKES, commentWrapper.getCommentList().get(0).getDislikes());
        assertEquals(LIKES, commentWrapper.getCommentList().get(0).getLikes());
        assertEquals(UserInteraction.NO_INTERACTION, commentWrapper.getCommentList().get(0).getUserInteraction());

    }

    @Test
    public void testRemoveCommentNotifyingUserWhenAnErrorHappensInDao(){
        CommentDeletionData commentDeletionData = new CommentDeletionData(USER_EMAIL, RECIPE_NAME, COMMENT);
        when(commentsDao.getCommentDeletionData( COMMENT_ID)).thenReturn(Optional.of(commentDeletionData));

        when(commentsDao.deleteComment(COMMENT_ID)).thenReturn(false);

        boolean result = cs.removeCommentNotifyingUser(RECIPE_ID, COMMENT_ID, DELETION_MOTIVE);
        assertFalse(result);
    }
    @Test
    public void testRemoveCommentNotifyingUserWhenCommentIsNotPresent(){
        when(commentsDao.getCommentDeletionData( COMMENT_ID)).thenReturn(Optional.empty());
        when(commentsDao.deleteComment(COMMENT_ID)).thenReturn(true);

        boolean result = cs.removeCommentNotifyingUser(RECIPE_ID, COMMENT_ID, DELETION_MOTIVE);
        assertFalse(result);
    }


}

