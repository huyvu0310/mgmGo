package com.mgmtp.internship.experiences.services;


import com.mgmtp.internship.experiences.dto.TagDTO;

import java.util.List;

public interface TagService {
    List<TagDTO> findListTagByContainContent(String tagContent, List<String> listAvailableContent);

    long insertTag(String content);

    boolean addListTagForActivity(long activityId, List<TagDTO> listTagDTO);
}
