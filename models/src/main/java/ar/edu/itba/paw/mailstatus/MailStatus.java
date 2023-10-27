package ar.edu.itba.paw.mailstatus;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MailStatus {
    private boolean send;
    private String error;

    public MailStatus(boolean send) {
        this.send = send;
    }
}
