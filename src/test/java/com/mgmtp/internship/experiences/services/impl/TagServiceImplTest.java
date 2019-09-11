package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.TagDTO;
import com.mgmtp.internship.experiences.repositories.TagRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceImplTest {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    private static final List<TagDTO> EXPECTED_LIST_TAG_DTO = new ArrayList<>();
    private static final List<String> LIST_AVAILABLE_CONTENT = Collections.emptyList();
    private static final Long TAG_ID = 1L;
    private static final long ACTIVITY_ID = 1L;
    private static final String CONTENT = "content";

    @Test
    public void shouldReturnListTagDTOIfFindListByContent() {
        Mockito.when(tagRepository.findListTagByContainContent(CONTENT, LIST_AVAILABLE_CONTENT)).thenReturn(EXPECTED_LIST_TAG_DTO);

        List<TagDTO> actualListTagDTO = tagService.findListTagByContainContent(CONTENT, LIST_AVAILABLE_CONTENT);

        Assert.assertEquals(EXPECTED_LIST_TAG_DTO, actualListTagDTO);
    }

    @Test
    public void shouldReturnTagIdIfInsertTagSuccessFully() {
        Mockito.when(tagRepository.insertTag(CONTENT)).thenReturn(TAG_ID);

        Long actualTagId = tagService.insertTag(CONTENT);

        Assert.assertEquals(TAG_ID, actualTagId);
    }

    @Test
    public void shouldReturnTrueIfAddListTagSuccessFully() {
        Mockito.when(tagRepository.addListTagForActivity(ACTIVITY_ID, EXPECTED_LIST_TAG_DTO)).thenReturn(true);

        boolean actual = tagService.addListTagForActivity(ACTIVITY_ID, EXPECTED_LIST_TAG_DTO);

        Assert.assertEquals(true, actual);
    }
}
