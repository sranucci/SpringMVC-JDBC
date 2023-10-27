package ar.edu.itba.paw.servicesInterface;

import ar.edu.itba.paw.mailstatus.MailStatus;

import java.util.concurrent.CompletableFuture;

public interface MailService {
    CompletableFuture<MailStatus> sendNewCommentEmail(String destination, String username, String commenter, String comment, Long recipeId);

    CompletableFuture<MailStatus> sendRecipeDeletionEmail(String destination, String recipeName, String body);

    CompletableFuture<MailStatus> sendNLikesEmail(String destination, String recipeNam, String username, long likes, Long recipeId);

    CompletableFuture<MailStatus> sendResetPassword(String destination, String token);

    CompletableFuture<MailStatus> sendCommentDeletionEmail(String destination, String username, String commentData, String deletionMotive);

    CompletableFuture<MailStatus> sendVerificationToken(String destination, String token, String username);

    CompletableFuture<MailStatus> sendSuccessfulPasswordChange(String email, String username);

}
