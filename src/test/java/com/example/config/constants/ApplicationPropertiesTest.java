package com.example.config.constants;

import static org.junit.Assert.*;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Unit tests for ApplicationProperties.
 * 
 * @author Swarn Avinash Kumar
 */
public class ApplicationPropertiesTest {

    /**
     * There's no much to test here. Let's be sure that we cannot instantiate an object of this class.
     *
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, SecurityException {
        Constructor<ApplicationProperties> constructor = ApplicationProperties.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

}
