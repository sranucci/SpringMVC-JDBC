package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserVerificationToken;
import ar.edu.itba.paw.servicesInterface.TokenService;
import ar.edu.itba.paw.servicesInterface.UserService;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotFoundException;
import ar.edu.itba.paw.servicesInterface.exceptions.UserNotLoggedInException;
import ar.edu.itba.paw.webapp.forms.EditProfileForm;
import ar.edu.itba.paw.webapp.forms.ForgotPasswordForm;
import ar.edu.itba.paw.webapp.forms.RegisterForm;
import ar.edu.itba.paw.webapp.forms.ResetPasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.paw.webapp.forms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
public class UserController extends BaseUserTemplateController {

    private UserService us;
    private AuthenticationManager authenticationManager;
    private AuthenticationProvider authenticationProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView registerForm(@ModelAttribute("registerForm") final RegisterForm registerForm) {
        return new ModelAndView("/register");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(@Valid @ModelAttribute("registerForm") final RegisterForm registerForm, final BindingResult errors) {
        if (errors.hasErrors())
            return registerForm(registerForm);

        final Optional<User> user = us.createUser(registerForm.getEmail(), registerForm.getPassword(), registerForm.getFirstName(), registerForm.getLastName());

        if (user.isPresent()) {
            LOGGER.info("Registered user with mail {} under id {}",user.get().getEmail(),user.get().getId());
            return new ModelAndView("redirect:/verifyAccountSend");
        }

        ModelAndView mav = new ModelAndView("/register");
        mav.addObject("error", true);
        return mav;
    }

    @RequestMapping(value = "/verifyAccountSend", method = RequestMethod.GET)
    public ModelAndView verifyAccount() {
        return new ModelAndView("/verifyAccountSend");
    }

    @RequestMapping(value = "/verifyAccount/{token}", method = RequestMethod.GET)
    public ModelAndView verifyAccount(@PathVariable("token") final String token) {

        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(token, null);
            auth = authenticationProvider.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return new ModelAndView("redirect:/");
        } catch (Exception e) {
            LOGGER.error("token provided does not exist: {}.reason\n{}",token,e.getMessage());
            return new ModelAndView("redirect:/invalidToken");
        }
    }


    @RequestMapping(value = "/accountVerified", method = RequestMethod.GET)
    public ModelAndView accountVerified() {
        return new ModelAndView("/accountVerified");
    }

    @RequestMapping(value = "/invalidToken", method = RequestMethod.GET)
    public ModelAndView invalidToken() {
        return new ModelAndView("/invalidToken");
    }


    private void loginUser(String email, String unHashedPassword) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, unHashedPassword);
        authentication = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return new ModelAndView("/login");
    }

    @RequestMapping(value = "/loginNotVerified", method = RequestMethod.GET)
    public ModelAndView loginNotVerified() {
        return new ModelAndView("/loginNotVerified");
    }

    @RequestMapping(value = "/resendVerification", method = RequestMethod.GET)
    public ModelAndView resendVerificationView() {
        return new ModelAndView("/resendVerification");
    }

    @RequestMapping(value = "/resendVerification", method = RequestMethod.POST)
    public ModelAndView resendVerification(@ModelAttribute("verifyAccount") final VerifyAccountForm verifyAccountForm, final BindingResult errors) {
        if (errors.hasErrors()) {
            return resendVerificationView();
        }

        final Optional<User> user = us.findByEmail(verifyAccountForm.getEmail());

        if (!user.isPresent()) {
            final ModelAndView mav = new ModelAndView("/resendVerification");
            mav.addObject("error", "resendVerification.error");
            return mav;
        }

        us.resendVerificationToken(user.get().getEmail());

        return new ModelAndView("redirect:/verifyAccountSend");
    }


    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public ModelAndView forgotPassword() {
        return new ModelAndView("/forgotPassword");
    }

    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ModelAndView forgotPassword(@Valid @ModelAttribute("forgotPasswordForm") final ForgotPasswordForm forgotPasswordForm, final BindingResult errors) {
        if (errors.hasErrors())
            return forgotPassword();

        final Optional<User> user = us.findByEmail(forgotPasswordForm.getEmail());
        if (!user.isPresent()) {
            errors.rejectValue("email", "forgotPassword.error.invalidEmail");
            return forgotPassword();
        }

        us.generateResetPasswordToken(user.get());
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping("/profile")
    public ModelAndView profile() {
        final ModelAndView mav = new ModelAndView("/profile");
        mav.addObject("user", us.findById(getCurrentUserId().orElseThrow(UserNotFoundException::new)).orElseThrow(UserNotFoundException::new));
        Long likes = us.getLikes(getCurrentUserId().orElseThrow(UserNotFoundException::new));
        long comments = us.getCountComments(getCurrentUserId().orElseThrow(UserNotFoundException::new));
        mav.addObject("likes", likes);
        mav.addObject("comments", comments);
        return mav;
    }


    @RequestMapping(value = "/editProfile", method = RequestMethod.GET)
    public ModelAndView editProfile(@ModelAttribute("editProfileForm") final EditProfileForm editProfileForm) {
        final ModelAndView mav = new ModelAndView("/editProfile");
        User user = us.findById(getCurrentUserId().orElseThrow(UserNotLoggedInException::new)).orElseThrow(UserNotFoundException::new);
        editProfileForm.setFirstName(user.getName());
        editProfileForm.setLastName(user.getLastname());
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/editProfile", method = RequestMethod.POST)
    public ModelAndView editProfile(@Valid @ModelAttribute("editProfileForm") final EditProfileForm editProfileForm, final BindingResult errors) throws IOException {
        User user = us.findById(getCurrentUserId().orElseThrow(UserNotFoundException::new)).orElseThrow(UserNotFoundException::new);

        if (errors.hasErrors())
            return editProfile(editProfileForm);

        us.updateProfile(user.getId(), editProfileForm.getFirstName(), editProfileForm.getLastName(), editProfileForm.getUserPhoto().getBytes(), editProfileForm.isDeleteProfilePhoto());
        LOGGER.info("User with id {} updated its profile",user.getId());
        return new ModelAndView("redirect:/profile");
    }

    @RequestMapping(value = "/resetPassword/{token}", method = RequestMethod.GET)
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, @PathVariable("token") final String token) {
        if (us.validateToken(token)) {
            return new ModelAndView("/resetPassword");
        }
        return new ModelAndView("redirect:/invalidToken");
    }

    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@Valid @ModelAttribute("resetPasswordForm") final ResetPasswordForm resetPasswordForm, final BindingResult errors, @ModelAttribute("token") final String token) {
        if (errors.hasErrors())
            return resetPassword(resetPasswordForm, resetPasswordForm.getToken());

        final Optional<User> user = us.resetPassword(resetPasswordForm.getEmail(), resetPasswordForm.getToken(), resetPasswordForm.getPassword());
        boolean success = false;

        if (user.isPresent()) {
            success = true;
            us.sendSuccessfulPasswordChange(user.get().getEmail());
            us.deleteGeneratedToken(token);
            loginUser(user.get().getEmail(), resetPasswordForm.getPassword());
        }

        final ModelAndView mav = new ModelAndView("/resetPasswordRequest");
        mav.addObject("success", success);
        return mav;
    }

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setUserService(UserService us) {
        this.us = us;
    }

    @Autowired
    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
    }
}
