package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.services.ActivityService;
import com.mgmtp.internship.experiences.services.FavoriteService;
import com.mgmtp.internship.experiences.services.TagService;
import com.mgmtp.internship.experiences.services.UserService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Test for Activity Controller
 *
 * @author vhduong
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityControllerTest.class);
    private static final String ACTIVITY_INFO_ATTRIBUTE = "activityDetailDTO";
    private static final long ID = 1l;
    private static final long IMAGE_ID = 1l;

    private static final String DISPLAY_NAME = "name";
    private static final int USER_REPUTATION_SCORE = 1;
    private static final long ACTIVITY_ID = 1;
    private static final String UPDATE_URL = "/activity/update";
    private static final String CREATE_URL = "/activity/create";
    private static final String CONTENT_PARAM = "content";

    private static final ActivityDetailDTO EXPECTED_ACTIVITY_DETAIL_DTO = ActivityTestUtil.prepareExpectedActivityDetailDTOWithNameForTest("name");
    private static final ActivityDetailDTO EXISTED_ACTIVITY_DETAIL_DTO = ActivityTestUtil.prepareExpectedActivityDetailDTOWithNameForTest("existedName");
    private static final String ERROR_ATTRIBUTE = "errorMessage";
    private static final String ERROR_PAGE = "error";
    private static final String DESC_PARAM = "description";
    private static final UserProfileDTO userProfileDTO = new UserProfileDTO(IMAGE_ID, DISPLAY_NAME, USER_REPUTATION_SCORE);
    private static final LdapUserDetails ldapUserDetails = mock(LdapUserDetails.class);
    private static final CustomLdapUserDetails EXPECTED_CUSTOM_USER_DETAIL = new CustomLdapUserDetails(ID, userProfileDTO, ldapUserDetails);
    private MockMvc mockMvc;

    @Mock
    private ActivityService activityService;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private ActivityController activityController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(activityController).build();
    }

    @Test
    public void shouldGetActivityShowOnActivityPage() {
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setFavorite(true);
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);
        Mockito.when(favoriteService.checkFavorite(ACTIVITY_ID, EXPECTED_CUSTOM_USER_DETAIL.getId())).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO.isFavorite());

        try {
            mockMvc.perform(get("/activity/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("activity/detail"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowErrorPageIfWrongActivityId() {
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(null);
        try {
            mockMvc.perform(get("/activity/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(ERROR_ATTRIBUTE, "Activity Not Found"))
                    .andExpect(view().name(ERROR_PAGE));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowEditPage() {
        Mockito.when(activityService.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);

        try {
            mockMvc.perform(get("/activity/update/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("activity/update"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnShowActivityIfUpdateSuccess() {
        int updateSuccess = 1;
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setUpdatedByUserId(EXPECTED_CUSTOM_USER_DETAIL.getId());
        Mockito.when(activityService.update(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(updateSuccess);
        Mockito.when(tagService.addListTagForActivity(Mockito.anyLong(), Mockito.anyList())).thenReturn(true);

        try {
            mockMvc.perform(post(UPDATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription())
                    .param("tags[0].id", "1")
                    .param("tags[0].content", CONTENT_PARAM))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/activity/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()))
                    .andExpect(redirectedUrl("/activity/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowMessageErrorIfUpdateExistName() {
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setCreatedByUserId(EXPECTED_CUSTOM_USER_DETAIL.getId());
        Mockito.when(activityService.checkExistNameForUpdate(EXPECTED_ACTIVITY_DETAIL_DTO.getId(), EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(EXISTED_ACTIVITY_DETAIL_DTO);

        try {
            mockMvc.perform(post(UPDATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute(ERROR_ATTRIBUTE, "This name already exists. Please choose a difference name from the activity below!"))
                    .andExpect(flash().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("redirect:/activity/update/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()))
                    .andExpect(redirectedUrl("/activity/update/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowMessageIfUpdateFail() {
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        Mockito.when(activityService.update(EXPECTED_ACTIVITY_DETAIL_DTO)).thenThrow(DataIntegrityViolationException.class);
        try {
            mockMvc.perform(post(UPDATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute(ERROR_ATTRIBUTE, "Can't update Activity. Try again!"))
                    .andExpect(flash().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("redirect:/activity/update/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()))
                    .andExpect(redirectedUrl("/activity/update/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowCreatePage() {
        try {
            mockMvc.perform(get(CREATE_URL))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute(ACTIVITY_INFO_ATTRIBUTE, new ActivityDetailDTO()))
                    .andExpect(view().name("activity/create"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnHomePageIfCreateSuccess() {
        Long insertSuccess = 1L;
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setCreatedByUserId(EXPECTED_CUSTOM_USER_DETAIL.getId());

        Mockito.when(activityService.create(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(insertSuccess);
        Mockito.when(activityService.getIdActivity(EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO.getId());
        Mockito.when(userService.calculateAndUpdateRepulationScore(EXPECTED_ACTIVITY_DETAIL_DTO.getCreatedByUserId(), ApplicationConstant.REPUTATION_SCORE_CREATE_ACTIVITY)).thenReturn(true);
        Mockito.when(tagService.addListTagForActivity(Mockito.anyLong(), Mockito.anyList())).thenReturn(true);


        try {
            mockMvc.perform(post(CREATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription())
                    .param("tags[0].id", "1")
                    .param("tags[0].content", CONTENT_PARAM))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(view().name("redirect:/activity/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()))
                    .andExpect(redirectedUrl("/activity/" + EXPECTED_ACTIVITY_DETAIL_DTO.getId()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

    @Test
    public void shouldShowMessageErrorIfCreateExistName() {
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setCreatedByUserId(EXPECTED_CUSTOM_USER_DETAIL.getId());
        Mockito.when(activityService.checkExistNameForCreate(EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(EXISTED_ACTIVITY_DETAIL_DTO);

        try {
            mockMvc.perform(post(CREATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription()))
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute(ERROR_ATTRIBUTE, "This name already exists. Please check existed activity before create the new one!"))
                    .andExpect(flash().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("redirect:/activity/create"))
                    .andExpect(redirectedUrl(CREATE_URL));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldShowMessageErrorIfCreateWithMore3Tags() throws Exception {
        Long insertSuccess = 1L;
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        EXPECTED_ACTIVITY_DETAIL_DTO.setCreatedByUserId(EXPECTED_CUSTOM_USER_DETAIL.getId());

        Mockito.when(activityService.create(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(insertSuccess);
        Mockito.when(tagService.addListTagForActivity(Mockito.anyLong(), Mockito.anyList())).thenReturn(false);


        mockMvc.perform(post(CREATE_URL)
                .param("id", ACTIVITY_ID + "")
                .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription())
                .param("tags[0].id", "1")
                .param("tags[0].content", CONTENT_PARAM)
                .param("tags[1].id", "2")
                .param("tags[1].content", CONTENT_PARAM)
                .param("tags[2].id", "3")
                .param("tags[2].content", CONTENT_PARAM)
                .param("tags[3].id", "4")
                .param("tags[3].content", CONTENT_PARAM))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute(ERROR_ATTRIBUTE, "Tags are not valid!"))
                .andExpect(view().name("redirect:/activity/create"))
                .andExpect(redirectedUrl(CREATE_URL));
    }

    @Test
    public void shouldShowMessageIfCreateFail() {
        Mockito.when(userService.getCurrentUser()).thenReturn(EXPECTED_CUSTOM_USER_DETAIL);
        Mockito.when(activityService.create(EXPECTED_ACTIVITY_DETAIL_DTO)).thenThrow(DataIntegrityViolationException.class);

        try {
            mockMvc.perform(post(CREATE_URL)
                    .param("id", ACTIVITY_ID + "")
                    .param("name", EXPECTED_ACTIVITY_DETAIL_DTO.getName())
                    .param(DESC_PARAM, EXPECTED_ACTIVITY_DETAIL_DTO.getDescription()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(flash().attribute(ERROR_ATTRIBUTE, "Can't create Activity. Try again!"))
                    .andExpect(flash().attribute(ACTIVITY_INFO_ATTRIBUTE, EXPECTED_ACTIVITY_DETAIL_DTO))
                    .andExpect(view().name("redirect:/activity/create"))
                    .andExpect(redirectedUrl(CREATE_URL));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}