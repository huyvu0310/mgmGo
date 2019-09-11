package com.mgmtp.internship.experiences.constants;

/**
 * Application Constant.
 *
 * @author ttkngo
 */
public enum EnumSort
{
    NEWEST_FIRST("newest_first"), ACTIVE_FIRST("active_first"), RATING_FIRST("rating_first");

    private String action;

    public String getAction()
    {
        return this.action;
    }

    private EnumSort(String action)
    {
        this.action = action;
    }
}