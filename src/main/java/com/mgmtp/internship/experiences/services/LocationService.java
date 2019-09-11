package com.mgmtp.internship.experiences.services;

public interface LocationService {
    String getRequestURL(String lat, String lng);
    String getGeocodingAddress(String lat, String lng);
}
