package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.impl.FavoriteServiceImpl;
import com.mgmtp.internship.experiences.services.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class FavoriteRestControllerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingRestControllerTest.class);

    private static final String UPDATE_FAVORITE_URL = "/api/favorite/activity/1";
    private static final long ACTIVITY_ID = 1;
    private static final String ACTIVITY_ID_PARAM = "activityId";
    private static final UserProfileDTO USER_PROFILE_DTO = new UserProfileDTO(1L, "name", 12);
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails CUSTOM_USER_DETAILS = new CustomLdapUserDetails(1L, USER_PROFILE_DTO, LDAP_USER_DETAILS);
    private static final String IS_FAVORITE_PARAM = "isFavorite";

    private MockMvc mockMvc;

    @Mock
    private FavoriteServiceImpl favoriteService;

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private FavoriteRestController favoriteRestController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteRestController).build();
    }

    @Test
    public void shouldReturnErrorMessageIfNotLogged() {
        when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(get(UPDATE_FAVORITE_URL)
                    .param(ACTIVITY_ID_PARAM, String.valueOf(ACTIVITY_ID))
                    .param(IS_FAVORITE_PARAM, String.valueOf(true)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Please login to perform this operation.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnAddFavoriteSuccessMessageIfWantToActivityIsFavoriteOfUserAndUpdateFavoriteSuccess() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.updateFavorite(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId(), true)).thenReturn(true);

        try {
            mockMvc.perform(get(UPDATE_FAVORITE_URL)
                    .param(ACTIVITY_ID_PARAM, String.valueOf(ACTIVITY_ID))
                    .param(IS_FAVORITE_PARAM, String.valueOf(true)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"status\":\"SUCCESS\",\"message\":\"Add favorite activity success!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnDeleteFavoriteSuccessMessageIfWantToActivityIsNotFavoriteOfUserAndUpdateFavoriteSuccess() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.updateFavorite(ACTIVITY_ID, CUSTOM_USER_DETAILS.getId(), false)).thenReturn(true);

        try {
            mockMvc.perform(get(UPDATE_FAVORITE_URL)
                    .param(ACTIVITY_ID_PARAM, String.valueOf(ACTIVITY_ID))
                    .param(IS_FAVORITE_PARAM, String.valueOf(false)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"status\":\"SUCCESS\",\"message\":\"Remove favorite activity success!\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnErrorMessageIfUpdateFavoriteFail() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.updateFavorite(-1, CUSTOM_USER_DETAILS.getId(), true)).thenReturn(false).thenThrow(ApiException.class);

        try {
            mockMvc.perform(get("/api/favorite/activity/-1")
                    .param(ACTIVITY_ID_PARAM, String.valueOf(ACTIVITY_ID))
                    .param(IS_FAVORITE_PARAM, String.valueOf(true)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("{\"status\":\"FAILED\",\"message\":\"Something went wrong! Please try again.\"}"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
