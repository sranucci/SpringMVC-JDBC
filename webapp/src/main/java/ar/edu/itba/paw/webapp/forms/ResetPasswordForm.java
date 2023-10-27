package ar.edu.itba.paw.webapp.forms;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ResetPasswordForm {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",message = "resetPasswordForm.email.error")
    private String email;

    @Size(min = 8, max = 100)
    private String password;

    @Size(min = 8, max = 100)
    private String repeatPassword;

    @NotEmpty
    private String token;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatch() {
        return password != null && password.equals(repeatPassword);
    }

}