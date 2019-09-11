package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.impl.ActivityServiceImpl;
import com.mgmtp.internship.experiences.services.impl.RatingServiceImpl;
import com.mgmtp.internship.experiences.services.impl.UserServiceImpl;
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

import static org.mockito.Mockito.mock;


/**
 * Unit test for rating rest controller.
 *
 * @author thuynh
 */
@RunWith(MockitoJUnitRunner.class)
public class RatingRestControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingRestControllerTest.class);

    private static final long USER_ID = 1L;
    private static final long IMAGE_ID = 1L;
    private static final String DISPLAY_NAME = "name";
    private static final int USER_REPUTATION_SCORE = 1;
    private static final int ACTIVITY_ID = 1;
    private static final String RATING_URL = "/rating/activity/1";
    private static final String RATING_PARAM = "rating";
    private static final int RATING = 5;
    private static final UserProfileDTO USER_PROFILE_DTO = new UserProfileDTO(IMAGE_ID, DISPLAY_NAME, USER_REPUTATION_SCORE);
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails CUSTOM_USER_DETAILS = new CustomLdapUserDetails(USER_ID, USER_PROFILE_DTO, LDAP_USER_DETAILS);
    private MockMvc mockMvc;

    @Mock
    private UserServiceImpl userService;
    @Mock
    private ActivityServiceImpl activityService;

    @Mock
    private RatingServiceImpl ratingService;

    @InjectMocks
    private RatingRestController ratingRestController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ratingRestController).build();
    }

    @Test
    public void shouldReturnUserRating() {
        int expectedRating = 5;
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(false);
        Mockito.when(ratingService.getRateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(expectedRating);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(RATING_URL))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"rating\":" + expectedRating + "}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorOnGetUserRatingIfNotLogged() {
        Mockito.when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(RATING_URL))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"Please login to perform this operation.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorMessageOnGetUserRatingIfActivityCreateByCurrentUser() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(true);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(RATING_URL))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"You can not rate your own activity.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnAverageRatingIfEditSuccess() {
        double expectedRating = 5.0;
        int updateSuccess = 1;
        int rating = 5;
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(false);
        Mockito.when(ratingService.editRateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId(), rating)).thenReturn(updateSuccess);
        Mockito.when(ratingService.getRate(ACTIVITY_ID)).thenReturn(expectedRating);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(RATING_URL)
                    .param(RATING_PARAM, String.valueOf(rating)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().string("{\"rating\":" + expectedRating + "}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorOnUpdateRatingIfNotLogged() {
        Mockito.when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(RATING_URL)
                    .param(RATING_PARAM, String.valueOf(RATING)))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"Please login to perform this operation.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnServerErrorOnUpdateIfEditFail() {
        int updateFail = 0;
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(false);
        Mockito.when(ratingService.editRateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId(), RATING)).thenReturn(updateFail).thenThrow(ApiException.class);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(RATING_URL)
                    .param(RATING_PARAM, String.valueOf(RATING)))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"Something went wrong! Please try again.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorMessageOnUpdateRatingIfActivityCreateByCurrentUser() {
        Mockito.when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        Mockito.when(activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId())).thenReturn(true);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(RATING_URL))
                    .andExpect(MockMvcResultMatchers.status().isForbidden())
                    .andExpect(MockMvcResultMatchers.content().string("{\"status\":\"FAILED\",\"message\":\"You can not rate your own activity.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
