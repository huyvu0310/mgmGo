package com.mgmtp.internship.experiences.constants;

/**
 * Application Constant.
 *
 * @author vhduong
 */
public class ApplicationConstant {
    public static final String REGEX_ALL_WHITESPACE_ENTER_TAB = "[ \\t\\n]{2,}";
    public static final String REGEX_ALL_WHITESPACE = "[ ]{2,}";

    public static final int REPUTATION_SCORE_CREATE_ACTIVITY = 10;
    public static final int REPUTATION_SCORE_UPLOAD_FIRST_PICTURE = 3;
    public static final int REPUTATION_SCORE_RATING_ACTIVITY_FIRST = 1;
    public static final int REPUTATION_SCORE_WRITING_COMMENT = 1;
    private static final long[] REPUTATION_LEVEL_SCORES = new long[]{0, 25, 50, 100, 250, 500};

    public static final int LIMIT_TAGS = 3;
    public static final int MAX_TAG_LENGTH = 20;

    public static final int MAX_NUMBER_REPORT = 5;
    public static final int REQUIRED_REPUTATION_REPORT = 20;

    public static final String FUNC_UNACCENT = "unaccent";

    public static final int RECORD_OF_LIST = 10;

    public static final int MAX_NUMBER_UPLOAD_IMAGES = 5;

    public static String getLevelReputation(long score) {
        for (int i = 1; i < REPUTATION_LEVEL_SCORES.length; i++) {
            if (score <= REPUTATION_LEVEL_SCORES[i]) {
                return "lv" + i;
            }
        }
        return "lv" + REPUTATION_LEVEL_SCORES.length;
    }

    private ApplicationConstant() {
    }
}
