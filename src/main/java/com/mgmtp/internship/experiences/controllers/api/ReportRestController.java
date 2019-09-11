package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.ApiResponse;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.ReportService;
import com.mgmtp.internship.experiences.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Report rest controller.
 *
 * @author vhduong
 */

@RestController
@RequestMapping("/report")
public class ReportRestController extends BaseRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("activity/{activityId}")
    public Object getReportActivity(@PathVariable("activityId") long activityId) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please login to perform this operation.");
        }
        return checkAllowReport(activityId, user.getId());
    }

    @PostMapping("/activity/{activityId}")
    public Object addReportActivity(@PathVariable("activityId") long activityId) {
        CustomLdapUserDetails user = userService.getCurrentUser();
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please login to perform this operation.");
        }
        ApiResponse refuseReport = checkAllowReport(activityId, user.getId());

        if (refuseReport.getResponseStatus() == ApiResponse.ApiResponseStatus.FAILED) {
            return refuseReport;
        }
        try {
            if (reportService.countReportOfActivity(activityId) < (ApplicationConstant.MAX_NUMBER_REPORT - 1)) {
                reportService.insertReport(activityId, user.getId());
            } else {
                activityService.deleteActivity(activityId);
            }
            return ApiResponse.success("Report success!");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Something went wrong. Please try again!");
        }
    }

    private ApiResponse checkAllowReport(long activityId, long userId) {
        if (userService.getReputationScoreById(userId) < ApplicationConstant.REQUIRED_REPUTATION_REPORT) {
            throw new ApiException(HttpStatus.ALREADY_REPORTED, "You need at least " + ApplicationConstant.REQUIRED_REPUTATION_REPORT + " reputation to use this feature!");
        }
        if (reportService.checkReportedByUser(activityId, userId)) {
            throw new ApiException(HttpStatus.ALREADY_REPORTED, "You've already reported this activity!");
        }
        if(activityService.findById(activityId) == null){
            throw new ApiException(HttpStatus.ALREADY_REPORTED, "Can't report this activity!");
        }
        return ApiResponse.success("");
    }
}
