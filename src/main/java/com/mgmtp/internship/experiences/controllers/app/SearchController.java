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
 * Search Controller.
 *
 * @author: ttkngo
 */

@Controller
@RequestMapping("/search")
public class SearchController {
    private static final String ACTIVITIES_ATTRIBUTE = "activities";

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public String search(Model model, @RequestParam(name = "searchInfor", required = false, defaultValue = "") String searchInfor,
                         @RequestParam(name = "sortType", required = false, defaultValue = "NEWEST_FIRST") String sortType,
                         @RequestParam(name = "filterTags", required = false) List<String> filterTags) {
        PageDTO pagingInfo;
        if (searchInfor.isEmpty()) {
            model.addAttribute(ACTIVITIES_ATTRIBUTE, activityService.getActivities(1, EnumSort.valueOf(sortType), filterTags));
            pagingInfo = new PageDTO(activityService.countTotalRecordActivity(filterTags));
        } else {
            model.addAttribute(ACTIVITIES_ATTRIBUTE, activityService.search(searchInfor, 1, EnumSort.valueOf(sortType), filterTags));
            pagingInfo = new PageDTO(activityService.countTotalRecordSearch(searchInfor, filterTags));
            model.addAttribute("keySearch", searchInfor.trim());
        }
        model.addAttribute("pagingInfo", pagingInfo);
        model.addAttribute("sortType", sortType);
        return "search";
    }

    @GetMapping("/more/{currentPage}")
    public String searchMoreActivities(@PathVariable int currentPage, Model model, @RequestParam(name = "searchInfor", required = false, defaultValue = "") String searchInfor,
                                       @RequestParam(name = "sortType", required = false, defaultValue = "NEWEST_FIRST") String sortType,
                                       @RequestParam(name = "filterTags", required = false) List<String> filterTags) {
        model.addAttribute(ACTIVITIES_ATTRIBUTE, activityService.search(searchInfor, currentPage, EnumSort.valueOf(sortType), filterTags));
        return "activity/fragments/list-activities";
    }
}
