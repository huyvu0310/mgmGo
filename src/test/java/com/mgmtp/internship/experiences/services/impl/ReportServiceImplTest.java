package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.repositories.ReportRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private static final long ACTIVITY_ID  = 1L;
    private static final long USER_ID = 1L;

    @Test
    public void shouldReturnTrueIfInsertReportSuccess(){
        Mockito.when(reportRepository.insertReport(ACTIVITY_ID, USER_ID)).thenReturn(true);

        Assert.assertEquals(true, reportService.insertReport(ACTIVITY_ID, USER_ID));
    }

    @Test
    public void shouldReturnFalseIfInsertReportSuccess(){
        Mockito.when(reportRepository.insertReport(ACTIVITY_ID, USER_ID)).thenReturn(false);

        Assert.assertEquals(false, reportService.insertReport(ACTIVITY_ID, USER_ID));
    }

    @Test
    public void shouldReturnTrueIfActivityIsReportedByUserId(){
        Mockito.when(reportRepository.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(true);

        Assert.assertEquals(true, reportService.checkReportedByUser(ACTIVITY_ID, USER_ID));
    }

    @Test
    public void shouldReturnFalseIfActivityIsReportedByUserId(){
        Mockito.when(reportRepository.checkReportedByUser(ACTIVITY_ID, USER_ID)).thenReturn(false);

        Assert.assertEquals(false, reportService.checkReportedByUser(ACTIVITY_ID, USER_ID));
    }

    @Test
    public void shouldReturnNumberOfReportActivityByActivityId(){
        int numberReport = 3;
        Mockito.when(reportRepository.countReportOfActivity(ACTIVITY_ID)).thenReturn(numberReport);

        Assert.assertEquals(numberReport, reportService.countReportOfActivity(ACTIVITY_ID));
    }
}