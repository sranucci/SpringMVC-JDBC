package ar.edu.itba.paw.models.comments;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentLikeData {
    private final long commentId;
    private final long userId;
    private final boolean liked;


}
