package ar.edu.itba.paw.models.deletion;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDeletionData {
    private String userMail;
    private String recipeName;
    private String commentData;
}