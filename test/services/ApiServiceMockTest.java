package services;

import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * Simple tests for the ApiServiceMock class
 *
 * @author Whole group
 */
public class ApiServiceMockTest {

    @Test
    public void nullResponseTest() {
        ApiServiceMock service = new ApiServiceMock();
        assertNull(service.sendRequest(""));
    }
}
