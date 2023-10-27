package ar.edu.itba.paw.servicesInterface;

import ar.edu.itba.paw.models.ResetPasswordToken;
import ar.edu.itba.paw.models.UserVerificationToken;

public interface TokenService {
    ResetPasswordToken generateResetPasswordToken(long id);
    UserVerificationToken generateUserVerificationToken(long userId);


    //no eliminar porque no buildea. es metodo scheduled
    void deleteNonVerifiedTokens();
}



