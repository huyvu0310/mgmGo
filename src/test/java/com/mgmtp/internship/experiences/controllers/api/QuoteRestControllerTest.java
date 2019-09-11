package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.dto.QuoteDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.impl.QuoteServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit Test for Rest Controller
 *
 * @author vhduong
 */
@RunWith(MockitoJUnitRunner.class)
public class QuoteRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuoteServiceImpl quoteService;

    @InjectMocks
    private QuoteRestController quoteRestController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quoteRestController).build();
    }

    @Test
    public void shouldReturnQuoteDTOIfNothingWrong() {
        QuoteDTO expectedQuoteDTO = new QuoteDTO("test", "test");
        Mockito.when(quoteService.getQuote()).thenReturn(expectedQuoteDTO);

        QuoteDTO actualQuoteDTO = quoteRestController.getQuote();

        Assert.assertEquals(actualQuoteDTO, expectedQuoteDTO);
    }

    @Test(expected = ApiException.class)
    public void shouldThrowExceptionIfResultNull() {
        Mockito.when(quoteService.getQuote()).thenReturn(null).thenThrow(ApiException.class);

        quoteRestController.getQuote();
    }

    @Test
    public void shouldReReturnPageError() throws Exception {
        Mockito.when(quoteService.getQuote()).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/quote"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}