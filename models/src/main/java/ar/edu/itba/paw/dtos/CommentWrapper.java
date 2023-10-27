package ar.edu.itba.paw.dtos;

import ar.edu.itba.paw.models.UserComment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CommentWrapper {
    private final List<UserComment> commentList; //this has a SQL LIMIT
    private final long totalComments; //totalComments without limit
}
