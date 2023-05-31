package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void createStudentService() {
        studentService.createStudent("Bia", "Coelho", "biacoelho@pm.me");
        CollegeStudent student = studentDao.findByEmailAddress("biacoelho@pm.me");
        assertEquals("biacoelho@pm.me", student.getEmailAddress(), "should find by email");
    }

    @Test
    public void isStudentNullCheck() {
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @Test
    public void deleteStudentService() {
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Should return true");
        assertTrue(deletedMathGrade.isPresent());
        assertTrue(deletedScienceGrade.isPresent());
        assertTrue(deletedHistoryGrade.isPresent());

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedMathGrade.isPresent(), "Should return false");
        assertFalse(deletedCollegeStudent.isPresent());
        assertFalse(deletedScienceGrade.isPresent());
        assertFalse(deletedHistoryGrade.isPresent());
    }

    // The @BeforeEach will execute first. then it will execute @Sql
    @Sql("/insertData.sql")
    @Test
    public void getGradebookService() {
        Iterable<CollegeStudent> iterableCollegeStudents = studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for (CollegeStudent collegeStudent : iterableCollegeStudents) {
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService() {
        // create the grade
        assertTrue(studentService.createGrade(80.50,1, "math"));
        assertTrue(studentService.createGrade(80.50,1, "science"));
        assertTrue(studentService.createGrade(80.50,1, "history"));

        // get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        // verify there is grades
        assertTrue(mathGrades.iterator().hasNext(), "student must have math grades");
        assertTrue(scienceGrades.iterator().hasNext(), "student must have science grades");
        assertTrue(historyGrades.iterator().hasNext(), "student must have history grades");

        // verify how many grades exist
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2);
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2);
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2);
    }

    @DisplayName("Should Fail - out of requirements")
    @Test
    public void createGradeServiceReturnFalse() {
        assertFalse(studentService.createGrade(105,1,"math"));
        assertFalse(studentService.createGrade(-5,1,"math"));
        assertFalse(studentService.createGrade(80.50,2,"math"));
        assertFalse(studentService.createGrade(80.50,1,"literature"));
    }

    @Test
    public void deleteGradeService() {
        assertEquals(1, studentService.deleteGrade(1,"math"), "Returns student id after delete");
        assertEquals(1, studentService.deleteGrade(1,"science"), "Returns student id after delete");
        assertEquals(1, studentService.deleteGrade(1,"history"), "Returns student id after delete");
    }

    @DisplayName("invalid gradeId and invalid gradeType")
    @Test
    public void deleteGradeServiceReturnStudentIdOfZero() {
        int studentIdExpected = 0;
        int gradeId = 0;
        assertEquals(studentIdExpected, studentService.deleteGrade(gradeId, "science"), "No student should have 0 id");
        assertEquals(studentIdExpected, studentService.deleteGrade(1,"literature"), "No student should have a literature class");
    }

    @Test
    public void studentInformation() {
        int studentId = 1;
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(studentId);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Beatriz", gradebookCollegeStudent.getFirstname());
        assertEquals("Dadalto", gradebookCollegeStudent.getLastname());
        assertEquals("beadadalto@pm.me", gradebookCollegeStudent.getEmailAddress());

        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size() == 1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size() == 1);
    }

    @DisplayName("Student not found")
    @Test
    public void studentInformationServiceReturnNull() {
        GradebookCollegeStudent gradebookCollegeStudent = studentService.studentInformation(0);
        assertNull(gradebookCollegeStudent);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

}
