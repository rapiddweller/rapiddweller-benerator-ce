package com.rapiddweller.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class TaskExceptionTest {
    @Test
    public void testConstructor() {
        TaskException actualTaskException = new TaskException();
        assertEquals("com.rapiddweller.task.TaskException", actualTaskException.toString());
        assertNull(actualTaskException.getLocalizedMessage());
        assertNull(actualTaskException.getCause());
        assertNull(actualTaskException.getMessage());
        assertEquals(0, actualTaskException.getSuppressed().length);
    }

    @Test
    public void testConstructor2() {
        TaskException actualTaskException = new TaskException("An error occurred");
        assertEquals("com.rapiddweller.task.TaskException: An error occurred", actualTaskException.toString());
        assertEquals("An error occurred", actualTaskException.getLocalizedMessage());
        assertNull(actualTaskException.getCause());
        assertEquals("An error occurred", actualTaskException.getMessage());
        assertEquals(0, actualTaskException.getSuppressed().length);
    }

    @Test
    public void testConstructor3() {
        Throwable throwable = new Throwable();
        assertSame((new TaskException("An error occurred", throwable)).getCause(), throwable);
    }

    @Test
    public void testConstructor4() {
        Throwable throwable = new Throwable();
        assertSame((new TaskException(throwable)).getCause(), throwable);
    }
}

