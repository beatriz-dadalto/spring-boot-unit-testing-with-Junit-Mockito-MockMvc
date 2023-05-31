package com.luv2code.test;

import com.luv2code.test.models.CollegeStudent;
import com.luv2code.test.models.StudentGrades;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class ApplicationExampleTest {

    private static int count = 0;

    @Value("${info.app.name}")
    private String appInfo;
    @Value("${info.app.description}")
    private String appDescription;
    @Value("${info.app.version}")
    private String appVersion;
    @Value("${info.school.name}")
    private String schoolName;

    @Autowired
    CollegeStudent student;

    @Autowired
    StudentGrades studentGrades;

    @Autowired
    ApplicationContext context;

    @BeforeEach
    public void beforeEach() {
        count += 1;
        System.out.println("Testing: " + appInfo + " which is " + appDescription +
                " Version: " + appVersion + ". Execution of test method " + count);
        student.setFirstname("Beatriz");
        student.setLastname("Dadalto");
        student.setEmailAddress("biacoelho@pm.me");
        studentGrades.setMathGradeResults(new ArrayList<>(Arrays.asList(100.0,85.0,76.50,91.75)));
        student.setStudentGrades(studentGrades);
    }

    @DisplayName("Add grade results for student grades")
    @Test
    public void addGradeResultsForStudentGrades() {
        Double expected = 353.25;

        assertEquals(expected, studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults()));
    }

    @DisplayName("Add grade results for student grades not equal")
    @Test
    public void addGradeResultsForStudentGradesAssertNotEquals() {
        Double unexpected = 0.0;

        assertNotEquals(unexpected, studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults()));
    }

    @DisplayName("Is grade greater")
    @Test
    public void isGradeGreaterStudentGrades() {
        double gradeOne = 90.0;
        double gradeTwo = 75.0;

        assertTrue(studentGrades.isGradeGreater(gradeOne, gradeTwo), "Should be true");
    }

    @DisplayName("Is grade greater false")
    @Test
    public void isGradeGreaterStudentGradeAssertFalse() {
        assertFalse(studentGrades.isGradeGreater(89,92), "Should be false");
    }

    @DisplayName("Check null for student grades")
    @Test
    public void checkNullForStudentGrades() {
        assertNotNull(studentGrades.checkNull(student.getStudentGrades().getMathGradeResults()));
    }

    @DisplayName("Create student without grade init")
    @Test
    public void createStudentWithoutGradesInit() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        studentTwo.setFirstname("Catarina");
        studentTwo.setLastname("Dadalto");
        studentTwo.setEmailAddress("catdadalto@gmail.com");

        assertNotNull(studentTwo.getFirstname());
        assertNotNull(studentTwo.getLastname());
        assertNotNull(studentTwo.getEmailAddress());
        assertNull(studentGrades.checkNull(studentTwo.getStudentGrades()));
    }

    @DisplayName("Verify students are prototypes")
    @Test
    public void verifyStudentArePrototypes() {
        CollegeStudent studentTwo = context.getBean("collegeStudent", CollegeStudent.class);
        assertNotSame(student, studentTwo);
    }

    @DisplayName("Find grade point Average")
    @Test
    public void findGradePointAverage() {
        assertAll("Testing all assertEquals: ",
                () -> assertEquals(353.25, studentGrades.addGradeResultsForSingleClass(student.getStudentGrades().getMathGradeResults())),
                () -> assertEquals(88.31, studentGrades.findGradePointAverage(student.getStudentGrades().getMathGradeResults()))
        );
    }

}
