package com.mgmtp.internship.experiences.utils;

import static com.mgmtp.internship.experiences.constants.ApplicationConstant.RECORD_OF_LIST;

public class LazyLoading {

    public static int countPages(int totalRecord) {
        return (int) Math.ceil(totalRecord * 1.0 / RECORD_OF_LIST);
    }

    private LazyLoading() {
    }
}
