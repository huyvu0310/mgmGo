package com.mgmtp.internship.experiences.dto;

import com.mgmtp.internship.experiences.utils.LazyLoading;

import java.util.Objects;

public class PageDTO {
    private int currentPage;
    private int sizeOfPages;
    private int totalRecord;

    public PageDTO() {
        this.currentPage = 1;
        this.sizeOfPages = 0;
        this.totalRecord = 0;
    }

    public PageDTO(int currentPage, int sizeOfPages, int totalRecord) {
        this.currentPage = currentPage;
        this.sizeOfPages = sizeOfPages;
        this.totalRecord = totalRecord;
    }

    public PageDTO(int totalRecord){
        this.currentPage = 1;
        this.totalRecord = totalRecord;
        this.sizeOfPages = LazyLoading.countPages(totalRecord);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getSizeOfPages() {
        return sizeOfPages;
    }

    public void setSizeOfPages(int sizeOfPages) {
        this.sizeOfPages = sizeOfPages;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageDTO pageDTO = (PageDTO) o;
        return currentPage == pageDTO.currentPage &&
                sizeOfPages == pageDTO.sizeOfPages &&
                totalRecord == pageDTO.totalRecord;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPage, sizeOfPages, totalRecord);
    }
}
