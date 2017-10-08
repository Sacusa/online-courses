package twitter.staff;

import static twitter.staff.TestHelpers.assertFailure;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

public class FilterTestRunner {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    JUnitCore runner = null;
    
    @Before public void before() {
        runner = new JUnitCore();
    }
    
    @Test public void yourTests_staffBadImpl_Filter_alwaysReturnsEmpty() {
        assertFailure(runner.run(twitter.FilterTest.class));
    }
}
