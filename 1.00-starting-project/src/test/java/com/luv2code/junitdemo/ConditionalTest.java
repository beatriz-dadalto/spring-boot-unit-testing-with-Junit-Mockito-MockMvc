package com.luv2code.junitdemo;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;

public class ConditionalTest {

    @Test
    @Disabled("Don't run until JIRA #123 is resolved")
    void basicTest() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testForWindowsOnly() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testForMacOnly() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testForLinuxOnly() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.WINDOWS})
    void testForLinuxAndWindowsOnly() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void testForJava8Only() {
        // execute method and perform asserts
    }

    @Test
    @EnabledOnJre(JRE.JAVA_11)
    void testForJava11Only() {
        // execute method and perform asserts
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_8, max = JRE.JAVA_11)
    void testForJavaRange8To11() {
        // execute method and perform asserts
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_11)
    void testForJavaRangeMinimum11() {
        // execute method and perform asserts
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "LUV2CODE_ENV", matches = "DEV")
    void testOnlyForDevEnvironment() {
        // execute method and perform asserts
    }

    @Test
    @EnabledIfSystemProperty(named = "LUV2CODE_SYS_PROP", matches = "CI_CD_DEPLOY")
    void testOnlyForSystemProperty() {
        // execute method and perform asserts
    }

}
