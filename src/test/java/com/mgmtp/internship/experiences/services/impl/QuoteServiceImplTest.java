package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.QuoteDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for QuotesService.
 *
 * @author vduong
 */

@RunWith(MockitoJUnitRunner.class)
public class QuoteServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private QuoteServiceImpl quoteServiceIml;


    @Test
    public void shouldReturnQuotesDTO() {
        QuoteDTO expectedQuoteDTO = new QuoteDTO("abcccc", "def");
        QuoteDTO[] quoteDTOS = new QuoteDTO[]{expectedQuoteDTO};
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
                .thenReturn(quoteDTOS);

        QuoteDTO actualQuoteDTO = quoteServiceIml.getQuote();

        assertEquals(expectedQuoteDTO, actualQuoteDTO);
    }

    @Test
    public void shouldReturnNullIfQuoteEmpty() {
        QuoteDTO expectedQuoteDTO = null;
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.any()))
                .thenReturn(null);

        QuoteDTO actualQuoteDTO = quoteServiceIml.getQuote();

        assertEquals(expectedQuoteDTO, actualQuoteDTO);
    }

    @Test(expected = RestClientException.class)
    public void shouldThrowExceptionIfRequestTimeout() {
        Mockito.doThrow(RestClientException.class)
                .when(restTemplate).getForObject(Mockito.anyString(), Mockito.any());

        quoteServiceIml.getQuote();
    }


}
