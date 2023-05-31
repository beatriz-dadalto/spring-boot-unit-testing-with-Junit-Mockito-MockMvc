package com.luv2code.junitdemo;

import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.Simple.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DemoUtilsTest {

    DemoUtils demoUtils;

    @BeforeEach
    void setupBeforeEach() {
        demoUtils = new DemoUtils();
        System.out.println("@BeforeEach executes before the execution of each test method");
    }

    @AfterEach
    void tearDownAfterEach() {
        System.out.println("Running @AfterEach\n");
    }

    @BeforeAll
    static void setupBeforeEachClass() {
        System.out.println("@BeforeAll executes only once before ALL test methods execution in the class\n");
    }

    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("@AfterAll executes only once after all test methods execution in the class.");
    }

    @Test
    @DisplayName("Multiply")
    @Order(-1)
    void testMultiply() {
        int expected = 25;

        assertEquals(expected, demoUtils.multiply(5,5), "5*5 must be 25");
    }

    @DisplayName("Equals")
    @Order(3)
    @Test
    void testEquals() {
        int expected = 6;

        // execute
        int actual = demoUtils.add(2,4);

        // assert
        assertEquals(expected, actual, " 2 + 4 must be 6");
    }

    @Test
    @Order(1)
    void testNotEquals() {
        int unexpected = 8;

        // execute
        int actual = demoUtils.add(2,4);

        // assert
        assertNotEquals(unexpected, actual, " the result must be 8");
    }

    @Test
    void testNull() {
        String expected = null;

        // execute
        String actual = (String) demoUtils.checkNull(expected);

        // assert
        assertNull(actual, "Object should be null");
    }

    @Test
    void testNotNull() {
        String expected = "Beatriz";

        // execute
        String actual = (String) demoUtils.checkNull(expected);

        // assert
        assertNotNull(actual, "Object should not be null");
    }

    @DisplayName("is the Same Object")
    @Test
    void testSame() {
        String expected = demoUtils.getAcademy();
        String actual = demoUtils.getAcademyDuplicate();

        assertSame(expected,actual, "Objects should refer to same object.");
    }

    @Test
    @DisplayName("Not Same Object")
    void testNotSame() {
        String expected = "Beatriz";
        String actual = demoUtils.getAcademy();
        assertNotSame(expected, actual, "Objects should not refer to same object.");
    }

    @Test
    @DisplayName("True and False, compare two numbers")
    void testTrue() {
        int number1 = 25;
        int number2 = 15;

        assertTrue(demoUtils.isGreater(number1, number2), "This should return true");
        assertFalse(demoUtils.isGreater(number2, number1), "This should return false");
    }

    @Test
    @DisplayName("Array equals")
    void testArrayEquals() {
        String[] expected = {"A", "B", "C"};

        assertArrayEquals(expected, demoUtils.getFirstThreeLettersOfAlphabet(), "Arrays should be the same");
    }

    @DisplayName("Iterable equals")
    @Test
    void testIterableEquals() {
        List<String> expected = List.of("luv", "2", "code");

        assertIterableEquals(expected, demoUtils.getAcademyInList(), "Expected list should be same as actual list");
    }

    @Test
    @DisplayName("Lines match? compare 2 lists")
    void testLinesMatch() {
        List<String> expected = List.of("luv", "2", "code");

        assertLinesMatch(expected, demoUtils.getAcademyInList(), "Lines should match");
    }

    @DisplayName("Throws exception")
    @Test
    void testThrowsException(){

        assertThrows(Exception.class, () -> demoUtils.throwException(-1), "Should throw exception");
    }

    @DisplayName("Does not Throws exception")
    @Test
    void testDoesNotThrowsException(){

        assertDoesNotThrow(() -> {
            demoUtils.throwException(5);
        }, "Should not throw exception");
    }

    @Test
    @DisplayName("Timeout")
    void testTimeout() {
        assertTimeoutPreemptively(Duration.ofSeconds(3), () -> demoUtils.checkTimeout(), "should execute in 3 seconds");
    }




}
