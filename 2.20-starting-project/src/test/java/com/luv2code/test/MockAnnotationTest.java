package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades studentGrades;

    /*
     * use @MockBean and @Autowired instead
     * @Mock and @InjectMocks
     */

    // create a mock for the DAO
    // @Mock
    @MockBean
    private ApplicationDao applicationDao;

    // inject mock dependencies
    //@InjectMocks
    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void beforeEach() {
        studentOne.setFirstname("Beatriz");
        studentOne.setLastname("Dadalto");
        studentOne.setEmailAddress("biacoelho@pm.me");
        studentOne.setStudentGrades(studentGrades);
    }

    @DisplayName("When & Verify")
    @Test
    public void assertEqualsTestAddGrades() {
        // execute
        when(applicationDao.addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults())).thenReturn(100.0);
        // assert
        assertEquals(100, applicationService.addGradeResultsForSingleClass(
                studentOne.getStudentGrades().getMathGradeResults()));

        // verify the DAO method was called
        verify(applicationDao).addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults());
        verify(applicationDao, times(1)).addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults());
    }

    @DisplayName("Find Grade Point Average")
    @Test
    public void assertEqualsTestFindGpa() {
        // execute
        when(applicationDao.findGradePointAverage(
                studentGrades.getMathGradeResults())).thenReturn(88.31);
        // assert
        assertEquals(88.31, applicationService.findGradePointAverage(
                studentOne.getStudentGrades().getMathGradeResults()));
    }

    @DisplayName("Not Null")
    @Test
    public void testAssertNotNull() {
        // execute
        when(applicationDao.checkNull(studentGrades.getMathGradeResults())).thenReturn(true);
        // assert
        assertNotNull(applicationService.checkNull(
                studentOne.getStudentGrades().getMathGradeResults()), "Should not be null");
    }

    @DisplayName("Throw runtime error")
    @Test
    public void throwRuntimeError() {
        // set up
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");
        // execute
        doThrow(new RuntimeException()).when(applicationDao).checkNull(nullStudent);
        // assert
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        // verify the checkNull method was called
        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @DisplayName("Multiple stubbing")
    @Test
    public void stubbingConsecutiveCalls() {
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");

        // first call throw exception. Consecutive calls do NOT throw exception
        when(applicationDao.checkNull(nullStudent))
                .thenThrow(new RuntimeException())
                .thenReturn("Do not throw exception second time");

        // first call
        assertThrows(RuntimeException.class, () -> applicationService.checkNull(nullStudent));
        // second call
        assertEquals("Do not throw exception second time", applicationService.checkNull(nullStudent));
        // verify the checkNull method was called two times
        verify(applicationDao, times(2)).checkNull(nullStudent);
    }

}
