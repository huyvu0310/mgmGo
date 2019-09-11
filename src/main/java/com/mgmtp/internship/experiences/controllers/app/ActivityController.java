package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.CommentDTO;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.FavoriteService;
import com.mgmtp.internship.experiences.services.TagService;
import com.mgmtp.internship.experiences.services.UserService;
import com.mgmtp.internship.experiences.utils.StringReplaceByRegexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

/**
 * Activity Controller.
 *
 * @author: vhduong
 */

@Controller
@RequestMapping("/activity")
public class ActivityController {

    private static final String ACTIVITY_INFO_ATTRIBUTE = "activityDetailDTO";
    private static final String USER_CREATED_INFO_ATTRIBUTE = "userCreatedInfo";
    private static final String COMMENT_INFO_ATTRIBUTE = "commentDTO";
    private static final String ERROR_VIEW = "error";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String ACTIVITY_NOT_FOUND = "Activity Not Found";
    private static final String REDIRECT_UPDATE_URL = "redirect:/activity/update/";
    private static final String REDIRECT_CREATE_URL = "redirect:/activity/create";
    private static final String REDIRECT_ACTIVITY_URL = "redirect:/activity/";
    private static final String MAX_NUMBER_UPLOAD_IMAGES = "maxNumberUploadImages";


    @Autowired
    private TagService tagService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @GetMapping("/{activityId}")
    public String getActivity(Model model, @PathVariable("activityId") long activityId) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        ActivityDetailDTO activityDetailDTO = activityService.findById(activityId);
        if (activityDetailDTO != null) {
            activityDetailDTO.setFavorite(favoriteService.checkFavorite(activityId, user.getId()));
            UserProfileDTO userProfileDTO = userService.findUserProfileById(activityDetailDTO.getCreatedByUserId());
            model.addAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            model.addAttribute(USER_CREATED_INFO_ATTRIBUTE, userProfileDTO);
            model.addAttribute("pagingInfo", new PageDTO(activityService.countTotalRecordCommentById(activityId)));
            model.addAttribute(COMMENT_INFO_ATTRIBUTE, new CommentDTO());
            model.addAttribute("comments", activityService.getComments(1, activityId));
            model.addAttribute(MAX_NUMBER_UPLOAD_IMAGES, ApplicationConstant.MAX_NUMBER_UPLOAD_IMAGES);

            return "activity/detail";
        }
        model.addAttribute(ERROR_MESSAGE, ACTIVITY_NOT_FOUND);
        return ERROR_VIEW;
    }

    @GetMapping("/update/{activityId}")
    public String getUpdateActivity(Model model, @PathVariable("activityId") long activityId) {
        if (!model.containsAttribute(ACTIVITY_INFO_ATTRIBUTE)) {
            ActivityDetailDTO activityDetailDTO = activityService.findById(activityId);
            if (activityDetailDTO == null) {
                model.addAttribute(ERROR_MESSAGE, ACTIVITY_NOT_FOUND);
                return ERROR_VIEW;
            }
            model.addAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
        }
        return "activity/update";
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, "name", new StringReplaceByRegexEditor(true, ApplicationConstant.REGEX_ALL_WHITESPACE_ENTER_TAB));
        dataBinder.registerCustomEditor(String.class, "description", new StringReplaceByRegexEditor(true, ApplicationConstant.REGEX_ALL_WHITESPACE));
        dataBinder.registerCustomEditor(String.class, "address", new StringReplaceByRegexEditor(true, ApplicationConstant.REGEX_ALL_WHITESPACE_ENTER_TAB));
    }

    @PostMapping("/update")
    public String updateActivity(@ModelAttribute(ACTIVITY_INFO_ATTRIBUTE) @Valid ActivityDetailDTO activityDetailDTO,
                                 final BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + ACTIVITY_INFO_ATTRIBUTE, bindingResult);
            redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            return REDIRECT_UPDATE_URL + activityDetailDTO.getId();
        }

        try {
            CustomLdapUserDetails user = userService.getCurrentUser();
            activityDetailDTO.setUpdatedByUserId(user.getId());
            ActivityDetailDTO existedActivity = activityService.checkExistNameForUpdate(activityDetailDTO.getId(), activityDetailDTO.getName());
            if (existedActivity == null) {
                activityService.update(activityDetailDTO);

                if (!tagService.addListTagForActivity(activityDetailDTO.getId(), activityDetailDTO.getTags())) {
                    redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Tags are not valid!");
                    redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
                    return REDIRECT_UPDATE_URL + activityDetailDTO.getId();
                }

                redirectAttributes.addFlashAttribute("successCrud", "Update activity success!");
                return REDIRECT_ACTIVITY_URL + activityDetailDTO.getId();
            }
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "This name already exists. Please choose a difference name from the activity below!");
            redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            redirectAttributes.addFlashAttribute("existedActivity", existedActivity);
            return REDIRECT_UPDATE_URL + activityDetailDTO.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Can't update Activity. Try again!");
            redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            return REDIRECT_UPDATE_URL + activityDetailDTO.getId();
        }
    }

    @GetMapping("/create")
    public String getCreateActivity(Model model) {
        if (!model.containsAttribute(ACTIVITY_INFO_ATTRIBUTE)) {
            model.addAttribute(ACTIVITY_INFO_ATTRIBUTE, new ActivityDetailDTO());
        }
        return "activity/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute(ACTIVITY_INFO_ATTRIBUTE) @Valid ActivityDetailDTO activityDetailDTO,
                         final BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + ACTIVITY_INFO_ATTRIBUTE, bindingResult);
            redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            return REDIRECT_CREATE_URL;
        }
        try {
            CustomLdapUserDetails user = userService.getCurrentUser();
            activityDetailDTO.setCreatedByUserId(user.getId());
            ActivityDetailDTO existedActivity = activityService.checkExistNameForCreate(activityDetailDTO.getName());
            if (existedActivity != null) {
                redirectAttributes.addFlashAttribute("existedActivity", existedActivity);
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "This name already exists. Please check existed activity before create the new one!");
                redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
                return REDIRECT_CREATE_URL;
            }

            Long activityId = activityService.create(activityDetailDTO);
            userService.calculateAndUpdateRepulationScore(activityDetailDTO.getCreatedByUserId(), ApplicationConstant.REPUTATION_SCORE_CREATE_ACTIVITY);

            if (!tagService.addListTagForActivity(activityId, activityDetailDTO.getTags())) {
                redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Tags are not valid!");
                redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
                return REDIRECT_CREATE_URL;
            }

            redirectAttributes.addFlashAttribute("successCrud", "Create activity success!");
            return REDIRECT_ACTIVITY_URL + activityService.getIdActivity(activityDetailDTO.getName());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ERROR_MESSAGE, "Can't create Activity. Try again!");
            redirectAttributes.addFlashAttribute(ACTIVITY_INFO_ATTRIBUTE, activityDetailDTO);
            return REDIRECT_CREATE_URL;
        }
    }
}