package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.impl.FavoriteServiceImpl;
import com.mgmtp.internship.experiences.services.impl.UserServiceImpl;
import com.mgmtp.internship.experiences.utils.LazyLoading;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for user profile controller.
 *
 * @author thuynh
 */
@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileControllerTest.class);

    private static final long USER_ID = 1L;
    private static final long IMAGE_ID = 1l;
    private static final String DISPLAY_NAME = "name";
    private static final int USER_REPUTATION_SCORE = 1;
    private static final String PROFILE_URL = "/profile/userprofile";
    private static final String USER_PROFILE_MODEL_TAG = "userProfileDTO";
    private static final String USERNAME_MODEL_TAG = "username";
    private static final String DISPLAY_NAME_FIELD = "displayName";
    private static final String IMAGE_ID_FIELD = "imageId";
    private static final String EXPECTED_VIEW_NAME = "user/profile";
    private static final UserProfileDTO USER_PROFILE_DTO = new UserProfileDTO(IMAGE_ID, DISPLAY_NAME, USER_REPUTATION_SCORE);
    private static final LdapUserDetails LDAP_USER_DETAILS = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails CUSTOM_USER_DETAILS = new CustomLdapUserDetails(USER_ID, USER_PROFILE_DTO, LDAP_USER_DETAILS);
    private static final int TOTAL_RECORD = 30;
    private static final int CURRENT_PAGE = 1;
    private static final int PAGE_SIZE = LazyLoading.countPages(TOTAL_RECORD);
    private static final PageDTO PAGING_INFO_DTO = new PageDTO(CURRENT_PAGE, PAGE_SIZE, TOTAL_RECORD);
    private static final List<ActivityDTO> EXPECTED_ACTIVITIES = Collections.singletonList(new ActivityDTO(1L, "name", new ArrayList<>(), Collections.emptyList()));


    private MockMvc mockMvc;
    @Mock
    private UserServiceImpl userService;

    @Mock
    private FavoriteServiceImpl favoriteService;

    @InjectMocks
    private UserProfileController userProfileController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userProfileController).build();
    }

    @Test
    public void shouldGetProfileShowOnProfilePage() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);

        try {
            mockMvc.perform(get(PROFILE_URL))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(USER_PROFILE_MODEL_TAG, CUSTOM_USER_DETAILS.getUserProfileDTO()))
                    .andExpect(model().attribute(USERNAME_MODEL_TAG, CUSTOM_USER_DETAILS.getUsername()))
                    .andExpect(view().name(EXPECTED_VIEW_NAME));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldRedirectLoginPageIfNotLogged() {
        when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(get(PROFILE_URL))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldRedirectLoginPageOnUpdateProfileIfNotLogged() {
        when(userService.getCurrentUser()).thenReturn(null);

        try {
            mockMvc.perform(post(PROFILE_URL)
                    .param(IMAGE_ID_FIELD, String.valueOf(USER_PROFILE_DTO.getImageId()))
                    .param(DISPLAY_NAME_FIELD, USER_PROFILE_DTO.getDisplayName()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowProfilePageIfUpdateProfileSuccess() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(userService.checkExitDisplayName(USER_PROFILE_DTO.getDisplayName(), CUSTOM_USER_DETAILS.getId())).thenReturn(false);
        when(userService.updateProfile(CUSTOM_USER_DETAILS.getId(), USER_PROFILE_DTO)).thenReturn(true);

        try {
            mockMvc.perform(post(PROFILE_URL)
                    .param(IMAGE_ID_FIELD, String.valueOf(USER_PROFILE_DTO.getImageId()))
                    .param(DISPLAY_NAME_FIELD, USER_PROFILE_DTO.getDisplayName()))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("success", "Update profile success."))
                    .andExpect(model().attribute(USER_PROFILE_MODEL_TAG, USER_PROFILE_DTO))
                    .andExpect(model().attribute(USERNAME_MODEL_TAG, CUSTOM_USER_DETAILS.getUsername()))
                    .andExpect(view().name(EXPECTED_VIEW_NAME));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowErrorOnProfilePageIfUpdateProfileFailed() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(userService.checkExitDisplayName(USER_PROFILE_DTO.getDisplayName(), CUSTOM_USER_DETAILS.getId())).thenReturn(false);
        when(userService.updateProfile(CUSTOM_USER_DETAILS.getId(), USER_PROFILE_DTO)).thenReturn(false);

        try {
            mockMvc.perform(post(PROFILE_URL)
                    .param(IMAGE_ID_FIELD, String.valueOf(USER_PROFILE_DTO.getImageId()))
                    .param(DISPLAY_NAME_FIELD, USER_PROFILE_DTO.getDisplayName()))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("error", "Can't update profile."))
                    .andExpect(model().attribute(USER_PROFILE_MODEL_TAG, USER_PROFILE_DTO))
                    .andExpect(model().attribute(USERNAME_MODEL_TAG, CUSTOM_USER_DETAILS.getUsername()))
                    .andExpect(view().name(EXPECTED_VIEW_NAME));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowErrorOnProfilePageIfDisplayNameAlready() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(userService.checkExitDisplayName(USER_PROFILE_DTO.getDisplayName(), CUSTOM_USER_DETAILS.getId())).thenReturn(true);

        try {
            mockMvc.perform(post(PROFILE_URL)
                    .param(IMAGE_ID_FIELD, String.valueOf(USER_PROFILE_DTO.getImageId()))
                    .param(DISPLAY_NAME_FIELD, USER_PROFILE_DTO.getDisplayName()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrorCode(USER_PROFILE_MODEL_TAG, DISPLAY_NAME_FIELD, "error." + USER_PROFILE_MODEL_TAG))
                    .andExpect(model().attributeHasFieldErrors(USER_PROFILE_MODEL_TAG, DISPLAY_NAME_FIELD))
                    .andExpect(model().attribute(USER_PROFILE_MODEL_TAG, USER_PROFILE_DTO))
                    .andExpect(model().attribute(USERNAME_MODEL_TAG, CUSTOM_USER_DETAILS.getUsername()))
                    .andExpect(view().name(EXPECTED_VIEW_NAME));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldGetActivitiesShowOnFavoriteActivities() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.getFavoriteActivitiesByUserId(CUSTOM_USER_DETAILS.getId(), CURRENT_PAGE)).thenReturn(EXPECTED_ACTIVITIES);
        when(favoriteService.countTotalRecord(CUSTOM_USER_DETAILS.getId())).thenReturn(PAGING_INFO_DTO.getTotalRecord());

        try {
            mockMvc.perform(get("/profile/favorite"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("activities", EXPECTED_ACTIVITIES))
                    .andExpect(model().attribute("pagingInfo", PAGING_INFO_DTO))
                    .andExpect(MockMvcResultMatchers.view().name("user/favorite-activities"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldGetMoreFavoriteActivitiesShowOnFragmentListActivities() {
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.getFavoriteActivitiesByUserId(CUSTOM_USER_DETAILS.getId(), CURRENT_PAGE)).thenReturn(EXPECTED_ACTIVITIES);

        try {
            mockMvc.perform(get("/profile/favorite/more/1")
                    .param("currentPage", String.valueOf(CURRENT_PAGE)))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("activities", EXPECTED_ACTIVITIES))
                    .andExpect(view().name("activity/fragments/list-activities"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnEmptyListFavoriteActivitiesIfCurrentPageInCorrect() {
        List<ActivityDTO> expectedActivities = Collections.emptyList();
        when(userService.getCurrentUser()).thenReturn(CUSTOM_USER_DETAILS);
        when(favoriteService.getFavoriteActivitiesByUserId(CUSTOM_USER_DETAILS.getId(), 2)).thenReturn(expectedActivities);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/profile/favorite/more/2")
                    .param("currentPage", String.valueOf(2)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("activities", expectedActivities))
                    .andExpect(MockMvcResultMatchers.view().name("activity/fragments/list-activities"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
