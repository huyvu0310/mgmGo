package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.RatingService;
import com.mgmtp.internship.experiences.services.UserService;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Rating rest controller.
 *
 * @author thuynh
 */
@RestController
@RequestMapping("/rating")
public class RatingRestController extends BaseRestController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/activity/{activityId}")
    public Object getRate(@PathVariable("activityId") long activityId) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please login to perform this operation.");
        }
        if (activityService.checkIsActivityCreateByUserId(activityId, user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can not rate your own activity.");
        }
        JSONObject result = new JSONObject();
        result.put("rating", ratingService.getRateByUserId(activityId, user.getId()));
        return result;
    }

    @PostMapping("/activity/{activityId}")
    public Object editRateOfUser(@PathVariable("activityId") long activityId,
                                 @RequestParam("rating")
                                 @Min(value = 1, message = "Rating has to be greater than or equal to 1 ")
                                 @Max(value = 5, message = "Rating has to be less than or equal to 5 ")
                                         int rating) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please login to perform this operation.");
        }
        if (activityService.checkIsActivityCreateByUserId(activityId, user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You can not rate your own activity.");
        }
        int rateOfUser = ratingService.getRateByUserId(activityId, user.getId());
        if (ratingService.editRateByUserId(activityId, user.getId(), rating) == 1) {
            if (rateOfUser == 0) {
                userService.calculateAndUpdateRepulationScore(user.getId(), ApplicationConstant.REPUTATION_SCORE_RATING_ACTIVITY_FIRST);
            }
            JSONObject result = new JSONObject();
            result.put("rating", ratingService.getRate(activityId));
            activityService.updatedActiveDate(activityId);
            return result;
        }
        throw new ApiException(HttpStatus.BAD_REQUEST, "Something went wrong! Please try again.");
    }
}
