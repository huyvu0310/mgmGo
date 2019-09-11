package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.dto.TagDTO;
import com.mgmtp.internship.experiences.services.TagService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TagRestControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingRestControllerTest.class);
    private static final String CONTENT = "tag";
    private static final TagDTO[] tags = new TagDTO[]{new TagDTO(1, "tag1"), new TagDTO(2, "tag2")};
    private static final List<TagDTO> EXPECTED_LIST_TAG_DTO = Arrays.asList(tags);
    private static final List<String> LIST_AVAILABLE_CONTENT = Arrays.asList("list");
    private static final String CONTENT_PARAM = "content";

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagRestController tagRestController;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagRestController).build();
    }

    @Test
    public void shouldReturnListTagDTOWhenFindTagByContent() {
        Mockito.when(tagService.findListTagByContainContent(CONTENT, LIST_AVAILABLE_CONTENT)).thenReturn(EXPECTED_LIST_TAG_DTO);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/tag/")
                    .param(CONTENT_PARAM, CONTENT).param("listAvailableContent[]", LIST_AVAILABLE_CONTENT.toArray(new String[]{})))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("[{\"id\":1,\"content\":\"tag1\"},{\"id\":2,\"content\":\"tag2\"}]"))
                    .andReturn();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

}
