package com.luv2code.springmvc;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
public class GradebookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private StudentAndGradeService studentService;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;

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

    @Autowired
    private MathGradesDao mathGradesDao;

    // @BeforeAll always has to be static
    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chad");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "chad_darby@gmail.com");
    }

    @BeforeEach
    public void beforeEach() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {
        CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Roby", "eric_roby@gmail.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Chad", "Darby", "chad_darby@gmail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = mockMvc.perform((MockMvcRequestBuilders.get("/"))).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentHttpRequest() throws Exception {

        CollegeStudent studentOne = new CollegeStudent("Eric", "Roby", "eric_roby@gmail.com");
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        MvcResult mvcResult = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname", request.getParameterValues("firstname"))
                .param("lastname", request.getParameterValues("lastname"))
                .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad_darby@gmail.com");
        assertNotNull(verifyStudent, "Student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        // make sure student exists first
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
        .get("/delete/student/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        // make sure student was deleted
        assertFalse(studentDao.findById(1).isPresent());
    }

    @DisplayName("Delete student that does not exist")
    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
        .get("/delete/student/{id}", 0))
        .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @DisplayName("HTTP GET Request")
    @Test
    public void studentInformationHttpRequest() throws Exception {
        // make sure student exists
        assertTrue(studentDao.findById(1).isPresent());
        // perform HTTP GET Request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @DisplayName("invalid student id")
    @Test
    public void studentInformationHttpStudentDoesNotExistRequest() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @DisplayName("Post HTTP Request")
    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        GradebookCollegeStudent student = studentService.studentInformation(1);
        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        // create a new grade
        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
        .contentType(MediaType.APPLICATION_JSON)
        .param("grade", "85.00")
        .param("gradeType", "math")
        .param("studentId", "1")).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        // verify if the new grade was added
        student = studentService.studentInformation(1);
        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());
    }

    @DisplayName("Create Grade for invalid student id")
    @Test
    public void createValidGradeHttpRequestWithStudentDoesNotExistReturnEmptyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades").contentType(MediaType.APPLICATION_JSON)
        .param("grade", "85.00")
        .param("gradeType", "history")
        .param("studentId", "0")).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @DisplayName("Create Grade for invalid gradeType")
    @Test
    public void createNonValidGradeHttpRequestTypeDoesNotExistReturnEmptyResponse() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/grades").contentType(MediaType.APPLICATION_JSON)
        .param("grade", "85.00")
        .param("gradeType", "literature")
        .param("studentId", "1")).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @DisplayName("Delete grade Http Request")
    @Test
    public void deleteValidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradesDao.findById(1);
        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",1,"math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        // confirm grade was really deleted
        mathGrade = mathGradesDao.findById(1);
        assertFalse(mathGrade.isPresent());
    }

    @DisplayName("Delete grade for invalid grade id")
    @Test
    public void deleteValidGradeHttpRequestStudentIdDoesNotExistReturnEmptyResponse() throws Exception {
        Optional<MathGrade> mathGrade = mathGradesDao.findById(2);

        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}", 2, "math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @DisplayName("Delete grade when gradeType is invalid")
    @Test
    public void deleteNonValidGradeHttpRequest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",1,"literature"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

}
