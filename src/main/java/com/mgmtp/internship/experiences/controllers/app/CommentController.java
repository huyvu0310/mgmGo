package com.mgmtp.internship.experiences.controllers.app;


import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.CommentDTO;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.UserService;
import com.mgmtp.internship.experiences.utils.DateTimeUtil;
import com.mgmtp.internship.experiences.utils.StringReplaceByRegexEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * comment app controller
 *
 * @author hnguyen
 */

@Controller
@RequestMapping("/comment")
public class CommentController {

    private static final String COMMENT_INFO_ATTRIBUTE = "commentDTO";

    @Autowired
    private ActivityService activityService;

    @Autowired
    private UserService userService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(String.class, "content", new StringReplaceByRegexEditor(true, ApplicationConstant.REGEX_ALL_WHITESPACE));
    }

    @PostMapping("/activity/{activityId}")
    public String addComment(Model model, @ModelAttribute(COMMENT_INFO_ATTRIBUTE) @Valid CommentDTO commentDTO,
                             final BindingResult bindingResult,
                             @PathVariable("activityId") Long activityId) {
        if (bindingResult.hasErrors()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Something went wrong! Please try again.");
        }
        CustomLdapUserDetails currentUser = userService.getCurrentUser();
        commentDTO.setDateCreate(DateTimeUtil.getCurrentDate());
        boolean checkExistedCommentOfUserByInActivity = activityService.checkExistedCommentOfUserByInActivity(currentUser.getId(), activityId);
        if (activityService.addComment(commentDTO, activityId, currentUser.getId()) != 0) {
            model.addAttribute("pagingInfo", new PageDTO(activityService.countTotalRecordCommentById(activityId)));
            model.addAttribute("comments", activityService.getComments(1, activityId));
            model.addAttribute("activityId", activityId);
            if (!checkExistedCommentOfUserByInActivity) {
                userService.calculateAndUpdateRepulationScore(currentUser.getId(), ApplicationConstant.REPUTATION_SCORE_WRITING_COMMENT);
            }
            activityService.updatedActiveDate(activityId);
            return "activity/fragments/total_comment";
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Something went wrong! Please try again.");
    }

    @GetMapping("activity/{activityId}/more/{currentPage}")
    public String getComments(Model model, @PathVariable("activityId") Long activityId, @PathVariable int currentPage) {
        model.addAttribute("comments", activityService.getComments(currentPage, activityId));
        return "activity/fragments/comment";
    }
}
