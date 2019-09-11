package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.services.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * Location gps service.
 *
 * @author dhnguyen
 */
@Service
@PropertySource(value = {"classpath:application.properties"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.getlocation.url}")
    private String locationAPIUrl;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public String getGeocodingAddress(String lat, String lng) {
        String xmlString = restTemplate.getForObject(getRequestURL(lat, lng), String.class);
        if (xmlString != null) {
            HashMap<String, String> data = new HashMap<>();
            data.put("house_number", xmlString.contains("<house_number>") ? xmlString.substring(xmlString.indexOf("<house_number>") + 14, xmlString.indexOf("</house_number>")) : "");
            data.put("road", xmlString.contains("road") ? xmlString.substring(xmlString.indexOf("<road>") + 6, xmlString.indexOf("</road>")) : "");
            data.put("county", xmlString.contains("county") ? xmlString.substring(xmlString.indexOf("<county>") + 8, xmlString.indexOf("</county>")) : "");
            data.put("state", xmlString.contains("state") ? xmlString.substring(xmlString.indexOf("<state>") + 7, xmlString.indexOf("</state>")) : "");
            return data.get("house_number") + " " + data.get("road") + ", " + data.get("county") + ", " + data.get("state");
        } else {
            return null;
        }
    }

    @Override
    public String getRequestURL(String lat, String lng) {
        if (locationAPIUrl != null) {
            return String.format(locationAPIUrl, lat, lng);
        }
        return null;
    }
}