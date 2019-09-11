package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.constants.EnumSort;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * Home Controller.
 *
 * @author: thuynh
 */

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private ActivityService activityService;

    @GetMapping
    public String getHome(Model model, @RequestParam(name = "sortType", required = false, defaultValue = "NEWEST_FIRST") String sortType,
                          @RequestParam(name = "filterTags", required = false) List<String> filterTags) {
        model.addAttribute("activities", activityService.getActivities(1, EnumSort.valueOf(sortType), filterTags));
        PageDTO pagingInfo = new PageDTO(activityService.countTotalRecordActivity(filterTags));
        model.addAttribute("pagingInfo", pagingInfo);
        model.addAttribute("sortType", sortType);
        return "home/index";
    }

    @GetMapping("more/{currentPage}")
    public String getActivities(Model model, @PathVariable int currentPage,
                                @RequestParam(name = "sortType", required = false, defaultValue = "NEWEST_FIRST") String sortBy,
                                @RequestParam(name = "filterTags", required = false) List<String> filterTags) {
        model.addAttribute("activities", activityService.getActivities(currentPage, EnumSort.valueOf(sortBy), filterTags));
        return "activity/fragments/list-activities";
    }
}
