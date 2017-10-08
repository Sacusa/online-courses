package twitter.staff;

import static twitter.staff.TestHelpers.assertFailure;
import static twitter.staff.TestHelpers.assertSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import twitter.*;
import twitter.Filter.FilterVariant;

public class FilterTestRunner {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    JUnitCore runner = null;
    
    @Before public void before() {
        runner = new JUnitCore();
    }
    
    @Test public void yourTests_staffGoodImpl_Filter() {
        Filter.variant = FilterVariant.Good;
        assertSuccess(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_writtenByReturnsOnlyFirstResultBug() {
        Filter.variant = FilterVariant.BadWrittenByFirstResult;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_writtenByReturnsFullListBug() {
        Filter.variant = FilterVariant.BadWrittenByFullList;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_writtenByMustBeCaseInsensitiveBug() {
        Filter.variant = FilterVariant.BadWrittenByCaseSensitive;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_writtenByIndexOutOfBoundsBug() {
        Filter.variant = FilterVariant.BadWrittenByIOBX;
        assertFailure(runner.run(FilterTest.class));
    }
    
    // NOT POSSIBLE WITH REQUIREMENT ON NO DUPLICATE TWEETS
//    @Test public void yourTests_staffBadImpl_writtenByNoDuplicateTweetsBug() {
//        Filter.variant = FilterVariant.BadWrittenByDuplicateTweet;
//        assertFailure(runner.run(FilterTest.class));
//    }

    @Test public void yourTests_staffBadImpl_inTimespanReturnsFullListBug() {
        Filter.variant = FilterVariant.BadInTimespanFullList;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_inTimespanExclusiveTimespanBug() {
        Filter.variant = FilterVariant.BadInTimespanNonInclusive;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_inTimespanAssumesInOrderBug() {
        Filter.variant = FilterVariant.BadInTimespanOrder;
        assertFailure(runner.run(FilterTest.class));
    }
    
//    @Test public void yourTests_staffBadImpl_inTimespanMutatesOriginalBug() {
//        Filter.variant = FilterVariant.BadInTimespanMutateOriginal;
//        assertFailure(runner.run(FilterTest.class));
//    }

    @Test public void yourTests_staffBadImpl_containingReturnsFullListBug() {
        Filter.variant = FilterVariant.BadContainingFullList;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_containingMustBeCaseInsensitiveBug() {
        Filter.variant = FilterVariant.BadContainingCaseSensitive;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_containingReturnsAndNotOrBug() {
        Filter.variant = FilterVariant.BadContainingAndNotOr;
        assertFailure(runner.run(FilterTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_containingReturnsSubstringMatchBug() {
        Filter.variant = FilterVariant.BadContainingSubstring;
        assertFailure(runner.run(FilterTest.class));
    }

    // NOT NEEDED FOR OR IMPLEMENTATION
//    @Test public void yourTests_staffBadImpl_containingRepeatingWordsBug() {
//        Filter.variant = FilterVariant.BadContainingRepeatingWords;
//        assertFailure(runner.run(FilterTest.class));
//    }
}
