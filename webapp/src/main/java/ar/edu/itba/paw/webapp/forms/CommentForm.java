package ar.edu.itba.paw.webapp.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentForm {

    @Size(min = 1, max = 256, message = "Comment must contain between 1 and 256 characters")
    private String comment;

}
