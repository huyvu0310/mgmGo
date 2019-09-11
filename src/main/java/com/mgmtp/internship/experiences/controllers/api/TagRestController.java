package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.dto.TagDTO;
import com.mgmtp.internship.experiences.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Tag rest controller.
 *
 * @author htnguyen
 */

@RestController
@RequestMapping("/api/tag")
public class TagRestController extends BaseRestController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public List<TagDTO> findTagByContainContent(@RequestParam(value = "content", defaultValue = "") String content,
                                                @RequestParam(value = "listAvailableContent[]", required = false) List<String> listAvailableContent) {
        return tagService.findListTagByContainContent(content, listAvailableContent);
    }
}
