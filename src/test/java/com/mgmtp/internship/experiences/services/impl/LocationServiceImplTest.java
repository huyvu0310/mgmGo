package com.mgmtp.internship.experiences.services.impl;

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
 * Unit Test for LocationService.
 *
 * @author dhnguyen
 */

@RunWith(MockitoJUnitRunner.class)
public class LocationServiceImplTest {

    private static final String LAT = "16.0702674";
    private static final String LNG = "108.22071150000001";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LocationServiceImpl locationService;

    @Test
    public void shouldReturnLocationInformationIfGetLocationSuccess() {
        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<reversegeocode timestamp='Thu, 18 Jul 19 05:00:22 +0000' attribution='Data © OpenStreetMap contributors, " +
                "ODbL 1.0. http://www.openstreetmap.org/copyright' querystring='format=xml&amp;lat=16.0702674&amp;lon=108.22071150000001&amp;zoom=18&amp;addressdetails=1'>\n" +
                "<result place_id=\"238021480\" osm_type=\"way\" osm_id=\"623112297\"   ref=\"mgm technology partners Vietnam Co Ltd\" lat=\"16.07025165\" lon=\"108.22077585\" boundingbox=\"16.0701739,16.0703294,108.2206036,108.2209481\">" +
                "mgm technology partners Vietnam Co Ltd, 7, Đường Phan Đình Phùng, Hải Châu, Đà Nẵng, 0236, Việt Nam</result>" +
                "<addressparts><address29>mgm technology partners Vietnam Co Ltd</address29>" +
                "<house_number>7</house_number><road>Đường Phan Đình Phùng</road><county>Hải Châu</county>" +
                "<state>Đà Nẵng</state><postcode>0236</postcode><country>Việt Nam</country><country_code>vn</country_code></addressparts></reversegeocode>";
        Mockito.when(restTemplate.getForObject(locationService.getRequestURL(LAT, LNG), String.class)).thenReturn(expectedXml);

        String expectedAddress = "7 Đường Phan Đình Phùng, Hải Châu, Đà Nẵng";

        assertEquals(expectedAddress, locationService.getGeocodingAddress(LAT, LNG));
    }

    @Test
    public void shouldReturnNullIfGetLocationFail() {
        Mockito.when(restTemplate.getForObject(locationService.getRequestURL(LAT, LNG), String.class)).thenReturn(null);

        String expectedAddress = null;

        assertEquals(expectedAddress, locationService.getGeocodingAddress(LAT, LNG));
    }

    @Test(expected = RestClientException.class)
    public void shouldThrowExceptionIfRequestTimeout() {
        Mockito.doThrow(RestClientException.class)
                .when(restTemplate).getForObject(locationService.getRequestURL(LAT, LNG), String.class);

        locationService.getGeocodingAddress(LAT, LNG);
    }

}