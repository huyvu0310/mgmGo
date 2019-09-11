package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.FavoriteService;
import com.mgmtp.internship.experiences.services.UserService;
import com.mgmtp.internship.experiences.utils.StringReplaceByRegexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Activity Controller.
 *
 * @author: thuynh
 */

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private static final String USER_PROFILE_MODEL_TAG = "userProfileDTO";
    private static final String USERNAME_MODEL_TAG = "username";
    private static final String REDIRECT_LOGIN_URL = "redirect:/login";
    private static final String VIEW_NAME = "viewName";
    private static final String ACTIVITIES = "activities";

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    ActivityService activityService;

    @GetMapping("/userprofile")
    public String profile(Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            return REDIRECT_LOGIN_URL;
        }
        user.getUserProfileDTO().setReputatinScore(userService.getReputationScoreById(user.getId()));
        model.addAttribute(USER_PROFILE_MODEL_TAG, user.getUserProfileDTO());
        model.addAttribute(USERNAME_MODEL_TAG, user.getUsername());
        model.addAttribute(VIEW_NAME, "userProfile");
        return "user/profile";
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, new StringReplaceByRegexEditor(true, ApplicationConstant.REGEX_ALL_WHITESPACE_ENTER_TAB));
    }

    @PostMapping("/userprofile")
    public String updateProfile(@ModelAttribute(USER_PROFILE_MODEL_TAG) @Valid UserProfileDTO profile, final BindingResult bindingResult, Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            return REDIRECT_LOGIN_URL;
        }
        if (!bindingResult.hasErrors()) {
            if (userService.checkExitDisplayName(profile.getDisplayName(), user.getId())) {
                bindingResult.rejectValue("displayName", "error." + USER_PROFILE_MODEL_TAG, "This display name is already in use.");
            } else if (userService.updateProfile(user.getId(), profile)) {
                model.addAttribute("success", "Update profile success.");
            } else {
                model.addAttribute("error", "Can't update profile.");
            }
        }
        profile.setImageId(user.getUserProfileDTO().getImageId());
        profile.setReputatinScore(user.getUserProfileDTO().getReputationScore());
        model.addAttribute(USER_PROFILE_MODEL_TAG, profile);
        model.addAttribute(USERNAME_MODEL_TAG, userService.getCurrentUser().getUsername());
        model.addAttribute(VIEW_NAME, "userProfile");
        return "user/profile";
    }

    @GetMapping("/myactivities")
    public String showListMyActivity(Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        model.addAttribute(ACTIVITIES, activityService.getListActivityByUserId(user.getId(), 1));
        model.addAttribute("pagingInfo", new PageDTO(activityService.countTotalRecordActivitybyUserId(user.getId())));
        model.addAttribute(VIEW_NAME, "myActivities");
        return "user/myactivities";
    }

    @GetMapping("/myactivities/more/{currentPage}")
    public String getActivities(@PathVariable("currentPage") int currentPage, Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        model.addAttribute(ACTIVITIES, activityService.getListActivityByUserId(user.getId(), currentPage));
        model.addAttribute(VIEW_NAME, "myActivities");
        return "activity/fragments/list-activities";
    }

    @GetMapping("/favorite")
    public String getFavoriteActivities(Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            return REDIRECT_LOGIN_URL;
        }
        model.addAttribute("pagingInfo", new PageDTO(favoriteService.countTotalRecord(user.getId())));
        model.addAttribute(ACTIVITIES, favoriteService.getFavoriteActivitiesByUserId(user.getId(), 1));
        model.addAttribute(VIEW_NAME, "favorite");
        return "user/favorite-activities";
    }

    @GetMapping("/favorite/more/{currentPage}")
    public String getMoreFavoriteActivities(@PathVariable("currentPage") int currentPage, Model model) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            return REDIRECT_LOGIN_URL;
        }
        model.addAttribute(ACTIVITIES, favoriteService.getFavoriteActivitiesByUserId(user.getId(), currentPage));
        model.addAttribute(VIEW_NAME, "favorite");
        return "activity/fragments/list-activities";
    }
}
