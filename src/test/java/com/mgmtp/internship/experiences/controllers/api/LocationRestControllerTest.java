package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.impl.LocationServiceImpl;
import org.jooq.tools.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Unit Test for LocationRestController
 *
 * @author dhnguyen
 */

@RunWith(MockitoJUnitRunner.class)
public class LocationRestControllerTest {

    private static final String LAT = "16.0702674";
    private static final String LNG = "108.22071150000001";

    @Mock
    private LocationServiceImpl locationService;

    @InjectMocks
    private LocationRestController locationRestController;

    @Test
    public void shouldReturnAddressIfReverseSuccess() {
        String expectedAddress = "7 PCT";
        Mockito.when(locationService.getGeocodingAddress(LAT, LNG)).thenReturn(expectedAddress);

        String actualAddress = ((JSONObject) locationRestController.getGeocoding(LAT, LNG)).get("addressGeocoding").toString();

        assertEquals(expectedAddress, actualAddress);
    }

    @Test(expected = ApiException.class)
    public void shouldThrowExceptionIfResultNull() {
        Mockito.when(locationService.getGeocodingAddress(LAT, LNG)).thenReturn(null).thenThrow(ApiException.class);

        locationRestController.getGeocoding(LAT, LNG);
    }

}