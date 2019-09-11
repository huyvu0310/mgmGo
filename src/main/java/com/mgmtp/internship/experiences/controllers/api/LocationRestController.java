package com.mgmtp.internship.experiences.controllers.api;

import com.mgmtp.internship.experiences.exceptions.ApiException;
import com.mgmtp.internship.experiences.services.LocationService;
import org.jooq.tools.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Location gps RestController.
 *
 * @author dhnguyen
 */
@RestController
@RequestMapping("/location")
public class LocationRestController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/activity/{lat}/{lng}/")
    public Object getGeocoding(@PathVariable("lat") String lat, @PathVariable("lng") String lng) {
        try {
            String addressGPS = locationService.getGeocodingAddress(lat, lng);
            if (addressGPS == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Can not locate address.");
            }
            JSONObject result = new JSONObject();
            result.put("addressGeocoding", addressGPS);
            return result;
        } catch (ApiException api) {
            throw api;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "External API error.");
        }
    }

}
