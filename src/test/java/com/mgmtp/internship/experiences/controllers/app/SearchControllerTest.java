package com.mgmtp.internship.experiences.controllers.app;

import com.mgmtp.internship.experiences.constants.EnumSort;
import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.dto.PageDTO;
import com.mgmtp.internship.experiences.services.impl.ActivityServiceImpl;
import com.mgmtp.internship.experiences.utils.LazyLoading;
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

import java.util.Collections;
import java.util.List;

/**
 * Unit Test for Search Controller.
 *
 * @author ttkngo.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchControllerTest.class);
    private static final String URL = "/search/";
    private static final String URL_VIEW = "search";
    private static final String KEY_SEARCH = "abc";
    private static final List<ActivityDTO> EXPECTED_ACTIVITY_DTO = Collections.singletonList(new ActivityDTO(1L, "name", null, Collections.emptyList()));
    private static final int CURRENT_PAGE = 1;
    private static final int TOTAL_RECORD = 10;
    private static final int PAGE_SIZE = LazyLoading.countPages(TOTAL_RECORD);
    private static final String URL_SEE_MORE = "/search/more/1";
    private static final String VIEW_LIST_ACTIVITIES = "activity/fragments/list-activities";
    private static final String SEARCH_PARAM = "searchInfor";
    private static final String ACTIVITIES_ATTRIBUTE = "activities";
    private static final PageDTO PAGING_INFO_DTO = new PageDTO(CURRENT_PAGE, PAGE_SIZE, TOTAL_RECORD);
    private static final String SORT_TYPE = "NEWEST_FIRST";
    private static final List<String> FILTER_TAGS = null;

    private MockMvc mockMvc;
    @Mock
    private ActivityServiceImpl activityService;

    @InjectMocks
    private SearchController searchController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    public void shouldGetListActivitiesShowOnSearchPageIfKeySearchCorrect() {
        Mockito.when(activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(EXPECTED_ACTIVITY_DTO);
        Mockito.when(activityService.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS)).thenReturn(PAGING_INFO_DTO.getTotalRecord());
        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL).param(SEARCH_PARAM, KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("keySearch", KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.model().attribute(ACTIVITIES_ATTRIBUTE, EXPECTED_ACTIVITY_DTO))
                    .andExpect(MockMvcResultMatchers.model().attribute("pagingInfo", PAGING_INFO_DTO))
                    .andExpect(MockMvcResultMatchers.model().attribute("sortType", SORT_TYPE))
                    .andExpect(MockMvcResultMatchers.view().name(URL_VIEW));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldGetNullActivityShowOnSearchPageIfKeySearchIncorrect() {
        PageDTO expectedPagingInfo = new PageDTO(0, 0, 0);
        Mockito.when(activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(null);
        Mockito.when(activityService.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS)).thenReturn(expectedPagingInfo.getTotalRecord());
        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL).param(SEARCH_PARAM, KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute("keySearch", KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.model().attribute(ACTIVITIES_ATTRIBUTE, null))
                    .andExpect(MockMvcResultMatchers.model().attribute("pagingInfo", PAGING_INFO_DTO))
                    .andExpect(MockMvcResultMatchers.model().attribute("sortType", SORT_TYPE))
                    .andExpect(MockMvcResultMatchers.view().name(URL_VIEW));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldGetListActivitiesShowOnSearchPageIfKeySearchEmpty() {
        Mockito.when(activityService.getActivities(CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(EXPECTED_ACTIVITY_DTO);
        Mockito.when(activityService.countTotalRecordActivity(FILTER_TAGS)).thenReturn(PAGING_INFO_DTO.getTotalRecord());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL).param(SEARCH_PARAM, ""))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute(ACTIVITIES_ATTRIBUTE, EXPECTED_ACTIVITY_DTO))
                    .andExpect(MockMvcResultMatchers.model().attribute("pagingInfo", PAGING_INFO_DTO))
                    .andExpect(MockMvcResultMatchers.model().attribute("sortType", SORT_TYPE))
                    .andExpect(MockMvcResultMatchers.view().name(URL_VIEW));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldSearchActivitiesShowOnFragmentListActivitiesIfKeySearchCorrect() {
        Mockito.when(activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(EXPECTED_ACTIVITY_DTO);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL_SEE_MORE)
                    .param("currentPage", String.valueOf(CURRENT_PAGE))
                    .param(SEARCH_PARAM, KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute(ACTIVITIES_ATTRIBUTE, EXPECTED_ACTIVITY_DTO))
                    .andExpect(MockMvcResultMatchers.view().name(VIEW_LIST_ACTIVITIES));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Test
    public void shouldReturnEmptyLisIfKeySearchInCorrect() {
        List<ActivityDTO> expectedActivities = Collections.emptyList();
        Mockito.when(activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(expectedActivities);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get(URL_SEE_MORE)
                    .param("currentPage", String.valueOf(CURRENT_PAGE))
                    .param(SEARCH_PARAM, KEY_SEARCH))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.model().attribute(ACTIVITIES_ATTRIBUTE, expectedActivities))
                    .andExpect(MockMvcResultMatchers.view().name(VIEW_LIST_ACTIVITIES));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }
}
