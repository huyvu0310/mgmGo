package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.CommentDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.impl.ActivityServiceImpl;
import com.mgmtp.internship.experiences.services.impl.UserServiceImpl;
import com.mgmtp.internship.experiences.utils.DateTimeUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Test for Comment Controller.
 *
 * @author hnguyen.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommentControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityControllerTest.class);
    private static final Long ACTIVITY_ID = 1L;
    private static final CommentDTO EXPECTED_COMMENT_DTO = new CommentDTO(null, null, null, "content", DateTimeUtil.getCurrentDate());
    private static final UserProfileDTO USER_PROFILE_DTO = new UserProfileDTO(1L, "name", 3);
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails CUSTOM_USER_DETAILS = new CustomLdapUserDetails(1L, USER_PROFILE_DTO, LDAP_USER_DETAILS);
    private static final List<CommentDTO> EXPECTED_LIST_COMMENT_DTO = Collections.singletonList(new CommentDTO(1L, 1L, "displayName", "content", DateTimeUtil.getCurrentDate()));
    private static final String URL_SEE_MORE = "/comment/activity/1/more/1";
    private static final String VIEW_LIST_COMMENT = "activity/fragments/comment";
    private static final String VIEW_LIST_TOTAL_COMMENT = "activity/fragments/total_comment";
    private static final String LIST_COMMENT_ATTRIBUTE = "comments";
    private static final int CURRENT_PAGE = 1;

    private MockMvc mockMvc;

    @Mock
    private ActivityServiceImpl activityService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private CommentController commentController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    public void shouldReturnListCommentsShowOnFragmentCommentIfAddCommentSuccess() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.addComment(Mockito.any(CommentDTO.class), Mockito.eq(ACTIVITY_ID), Mockito.eq(CUSTOM_USER_DETAILS.getId()))).thenReturn(1);
        Mockito.when(activityService.getComments(1, ACTIVITY_ID)).thenReturn(EXPECTED_LIST_COMMENT_DTO);
        try {
            mockMvc.perform(post("/comment/activity/1")
                    .param("content", EXPECTED_COMMENT_DTO.getContent()))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(LIST_COMMENT_ATTRIBUTE, EXPECTED_LIST_COMMENT_DTO))
                    .andExpect(view().name(VIEW_LIST_TOTAL_COMMENT));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldGetCommentsShowOnFragmentListActivities() {
        Mockito.when(activityService.getComments(CURRENT_PAGE, ACTIVITY_ID)).thenReturn(EXPECTED_LIST_COMMENT_DTO);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL_SEE_MORE)
                    .param("currentPage", String.valueOf(CURRENT_PAGE))
                    .param("activityId", String.valueOf(ACTIVITY_ID)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute(LIST_COMMENT_ATTRIBUTE, EXPECTED_LIST_COMMENT_DTO))
                    .andExpect(MockMvcResultMatchers.view().name(VIEW_LIST_COMMENT));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}