package com.example.config;

import com.example.config.constants.ApplicationProperties;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Environment}.
 * 
 * @author Swarn Avinash Kumar
 */
public class EnvironmentTest {

    /** Object to be tested */
    private Environment environment;

    /**
     * Cleaning the target before each test.
     *
     * @throws Exception the exception
     * @author Swarn Avinash Kumar
     */
    @Before
    public void setUp() throws Exception {
        environment = Environment.getInstance();
    }

    /**
     * Given valid property key_get property value_should return expected.
     */
    @Test
    public final void givenValidPropertyKey_getPropertyValue_shouldReturnExpected() {
        assertNotNull(environment.getPropertyValue(ApplicationProperties.QUEUE_VISIBILITY_TIMEOUT));
    }

    /**
     * Given invalid property key_get property value_should return null.
     */
    @Test
    public final void givenInvalidPropertyKey_getPropertyValue_shouldReturnNull() {
        assertNull(environment.getPropertyValue("missing.property"));
    }

    /**
     * Given null property key_get property value_should throw illegal argument exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void givenNullPropertyKey_getPropertyValue_shouldThrowIllegalArgumentException() {
        environment.getPropertyValue(null);
    }

}
