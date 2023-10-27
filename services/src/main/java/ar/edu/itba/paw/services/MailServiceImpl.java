package ar.edu.itba.paw.services;

import ar.edu.itba.paw.mailstatus.MailStatus;
import ar.edu.itba.paw.servicesInterface.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public MailServiceImpl(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    private MailStatus sendEmail(String subject, String destination, String templateName, Context context) {

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            message.setFrom("Taste Tales <tastetales.mailing@gmail.com>");
            message.setTo(destination);
            message.setSubject(subject);
            final String htmlContent = templateEngine.process(templateName, context);
            message.setText(htmlContent, true);

        } catch (MessagingException e) {
            LOGGER.error("Error sending mail to {} stacktrace:\n{}",destination,e.getMessage());
            return new MailStatus(false, e.getMessage());
        }

        this.mailSender.send(mimeMessage);
        return new MailStatus(true);
    }

    @Override
    public CompletableFuture<MailStatus> sendNewCommentEmail(String destination, String username, String commenter, String comment, Long recipeId) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("commenter", commenter);
        context.setVariable("comment", comment);
        String url = "http://pawserver.it.itba.edu.ar/paw-2023a-05/" + "recipeDetail/" + recipeId.toString();
        context.setVariable("url", url);
        String subject = messageSource.getMessage("commentEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(subject, destination, "commentEmailTemplate", context));
    }

    private CompletableFuture<MailStatus> executeMailThread(Supplier<MailStatus> callback) {
        return CompletableFuture.supplyAsync(callback);
    }

    @Override
    public CompletableFuture<MailStatus> sendRecipeDeletionEmail(String destination, String recipeName, String body) {

        Context context = new Context();
        context.setVariable("recipeName", recipeName);
        context.setVariable("body", body);
        context.setVariable("url", "http://pawserver.it.itba.edu.ar/paw-2023a-05/");
        String subject = messageSource.getMessage("recipeDeletionEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(subject, destination, "deletionEmailTemplate", context));
    }

    @Override
    public CompletableFuture<MailStatus> sendNLikesEmail(String destination, String recipeName, String username, long likes, Long recipeId) {
        Context context = new Context();
        context.setVariable("recipeName", recipeName);
        context.setVariable("username", username);
        context.setVariable("likes", likes);
        String url = "http://pawserver.it.itba.edu.ar/paw-2023a-05/" + "recipeDetail/" + recipeId.toString();
        context.setVariable("url", url);
        String subject = messageSource.getMessage("likesEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(
                subject, destination, "likesEmailTemplate", context));
    }

    public CompletableFuture<MailStatus> sendResetPassword(String destination, String token) {
            String urlToken = "http://pawserver.it.itba.edu.ar/paw-2023a-05/resetPassword/" + token;
//            String urlToken = "http://localhost:8080/resetPassword/" + token;
            Context context = new Context();
            context.setVariable("email", destination);
            context.setVariable("url", urlToken);
            String subject = messageSource.getMessage("resetPassword.subject", new Object[]{}, locale);
            return executeMailThread(() -> sendEmail(
                    subject, destination, "resetPasswordConfirmation", context));
    }

    @Override
    public CompletableFuture<MailStatus> sendCommentDeletionEmail(String destination, String recipeName, String commentData, String deletionMotive) {
        Context context = new Context();
        context.setVariable("recipeName", recipeName);
        context.setVariable("deletionMotive", deletionMotive);
        context.setVariable("commentData", commentData);
        context.setVariable("url", "http://pawserver.it.itba.edu.ar/paw-2023a-05/");
        String subject = messageSource.getMessage("commentDeletionEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(subject, destination, "commentDeletionEmailTemplate", context));
    }

    @Override
    public CompletableFuture<MailStatus> sendVerificationToken(String destination, String token, String username) {
        String urlToken = "http://pawserver.it.itba.edu.ar/paw-2023a-05/verifyAccount/" + token;
//        String urlToken = "http://localhost:8080/verifyAccount/" + token;
        Context context = new Context();
        context.setVariable("url", urlToken);
        context.setVariable("username", username);
        String subject = messageSource.getMessage("verificationTokenEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(
                subject, destination, "verificationAccountEmailTemplate", context));
    }

    @Override
    public CompletableFuture<MailStatus> sendSuccessfulPasswordChange(String destination, String username) {
        String url = "http://pawserver.it.itba.edu.ar/paw-2023a-05";
//        String url = "http://localhost:8080";
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("url", url);
        String subject = messageSource.getMessage("successfulPasswordChangeEmail.subject", new Object[]{}, locale);
        return executeMailThread(() -> sendEmail(
                subject, destination, "successfulChangePasswordEmailTemplate", context));
    }
}
