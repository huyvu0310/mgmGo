package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.impl.ActivityServiceImpl;
import com.mgmtp.internship.experiences.services.impl.ReportServiceImpl;
import com.mgmtp.internship.experiences.services.impl.UserServiceImpl;
import com.mgmtp.internship.experiences.utils.ActivityTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for report rest controller.
 *
 * @author vhduong
 */

@RunWith(MockitoJUnitRunner.class)
public class ReportRestControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportRestControllerTest.class);

    private static final long USER_ID = 1L;
    private static final int USER_REPUTATION_SCORE = 1;
    private static final long IMAGE_ID = 1L;
    private static final String DISPLAY_NAME = "name";
    private static final String ACTIVITY_NAME = "name";
    private static final int ACTIVITY_ID = 1;
    private static final String REPORT_URL = "/report/activity/1";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final ActivityDetailDTO EXPECTED_ACTIVITY_DETAIL_DTO = ActivityTestUtil.prepareExpectedActivityDetailDTOWithNameForTest(ACTIVITY_NAME);
    private static final UserProfileDTO USER_PROFILE_DTO = new UserProfileDTO(IMAGE_ID, DISPLAY_NAME, USER_REPUTATION_SCORE);
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails CUSTOM_USER_DETAILS = new CustomLdapUserDetails(USER_ID, USER_PROFILE_DTO, LDAP_USER_DETAILS);
    private MockMvc mockMvc;


    @Mock
    private ReportServiceImpl reportService;

    @Mock
    private ActivityServiceImpl activityService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private ReportRestController reportRestController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportRestController).build();
    }


    @Test
    public void shouldReturnErrorOnGetUserIfNotLoggedWhenGet() {
        Mockito.when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(get(REPORT_URL))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Please login to perform this operation.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiRespondFailUnderReputation()   {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(10);

        try {
            mockMvc.perform(get(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isAlreadyReported())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"You need at least 20 reputation to use this feature!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiRespondFailAlreadyReported() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(true);

        try {
            mockMvc.perform(get(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isAlreadyReported())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"You've already reported this activity!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiRespondFailIfActivityNull(){
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(null);

        try {
            mockMvc.perform(get(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isAlreadyReported())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Can't report this activity!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiRespondSuccess() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);

        try {
            mockMvc.perform(get(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"SUCCESS\",\"message\":\"\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorOnGetUserIfNotLoggedWhenPost() {
        Mockito.when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(post(REPORT_URL))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Please login to perform this operation.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiSuccessIfReportSuccess() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);
        Mockito.when(reportService.countReportOfActivity(ACTIVITY_ID)).thenReturn(1);
        Mockito.when(reportService.insertReport(ACTIVITY_ID, USER_ID)).thenReturn(true);

        try {
            mockMvc.perform(post(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"SUCCESS\",\"message\":\"Report success!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiSuccessIfDeleteActivitySuccess() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);
        Mockito.when(reportService.countReportOfActivity(ACTIVITY_ID)).thenReturn(5);

        try {
            mockMvc.perform(post(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"SUCCESS\",\"message\":\"Report success!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnApiFailedIfReportFailed() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(userService.getReputationScoreById(USER_ID)).thenReturn(30);
        Mockito.when(reportService.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);
        Mockito.when(reportService.countReportOfActivity(ACTIVITY_ID)).thenReturn(5);
        Mockito.when(activityService.deleteActivity(ACTIVITY_ID)).thenThrow(DataIntegrityViolationException.class);

        try {
            mockMvc.perform(post(REPORT_URL))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(CONTENT_TYPE))
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Something went wrong. Please try again!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}