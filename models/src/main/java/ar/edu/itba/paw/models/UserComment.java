package ar.edu.itba.paw.models;

import ar.edu.itba.paw.enums.UserInteraction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Date;

@Getter
@RequiredArgsConstructor
public class UserComment { //retrieved comments
    private final long commentId;
    private final String userName;
    private final String userLastName;
    private final String comment;
    private final Instant createdAt;
    private final long userId;
    private final long dislikes;
    private final long likes;
    private final UserInteraction userInteraction;

    //this is just for the JSP, that can't understant Instant, just Date
    public Date getCreatedAt() {
        return Date.from(createdAt);
    }

    public boolean currentUserLikes() {
        return userInteraction.userLikes();
    }

    public boolean currentUserDislikes() {
        return userInteraction.userDislikes();
    }
}
