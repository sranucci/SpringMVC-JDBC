package ar.edu.itba.paw.models.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment { //comments to create

    private long recipeId;
    private long userId;
    private long commentId;
    private String commentContent;


}
