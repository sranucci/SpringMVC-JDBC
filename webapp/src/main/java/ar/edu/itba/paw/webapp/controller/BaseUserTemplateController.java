package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.webapp.auth.PawAuthUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

public abstract class BaseUserTemplateController {
    //user data for the screen to render
    private Optional<PawAuthUserDetails> getUserCredentials() {
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (o instanceof PawAuthUserDetails) {
            PawAuthUserDetails auth = (PawAuthUserDetails) o;
            return Optional.of(auth);
        }
        return Optional.empty();
    }


    @ModelAttribute("currentUser")
    public Optional<PawAuthUserDetails> getUserDto() {
        if (getUserCredentials().isPresent()) {
            return Optional.of(getUserCredentials().get());
        }
        return Optional.empty();
    }


    protected Optional<Long> getCurrentUserId() {
        Optional<PawAuthUserDetails> currentUser = getUserCredentials();
        return currentUser.map(PawAuthUserDetails::getId);
    }


    protected boolean currentUserIsAdmin() {
        Optional<PawAuthUserDetails> ud = getUserCredentials();
        return ud.map(PawAuthUserDetails::isAdmin).orElse(false);
    }

}
