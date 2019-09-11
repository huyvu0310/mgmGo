package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.dto.QuoteDTO;
import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quote api controller.
 *
 * @author thuynh
 */

@RestController
@RequestMapping("api/quote")
public class QuoteRestController extends BaseRestController {

    @Autowired
    private QuoteService quoteService;

    @GetMapping
    public QuoteDTO getQuote() {
        try {
            QuoteDTO result = quoteService.getQuote();
            if (result == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Cannot found quote.");
            }
            return result;
        } catch (ApiException api) {
            throw api;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.REQUEST_TIMEOUT, "External API error.");
        }
    }
}
