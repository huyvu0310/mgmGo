package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.TagDTO;
import com.mgmtp.internship.experiences.repositories.TagRepository;
import com.mgmtp.internship.experiences.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<TagDTO> findListTagByContainContent(String tagContent, List<String> listAvailableContent) {
        return tagRepository.findListTagByContainContent(tagContent, listAvailableContent);
    }

    @Override
    public long insertTag(String content) {
        return tagRepository.insertTag(content);
    }

    @Override
    public boolean addListTagForActivity(long activityId, List<TagDTO> listTagDTO) {
        return tagRepository.addListTagForActivity(activityId, listTagDTO);
    }
}
