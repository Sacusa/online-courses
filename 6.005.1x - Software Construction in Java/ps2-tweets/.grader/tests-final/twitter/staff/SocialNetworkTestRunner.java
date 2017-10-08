package twitter.staff;

import static twitter.staff.TestHelpers.assertFailure;
import static twitter.staff.TestHelpers.assertSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import twitter.*;
import twitter.SocialNetwork.SocialNetworkVariant;

public class SocialNetworkTestRunner {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    JUnitCore runner = null;
    
    @Before public void before() {
        runner = new JUnitCore();
    }
    
    
    @Test public void yourTests_staffGoodImpl_guessFollowsGraphReturnsEmptyNodes() {
        SocialNetwork.variant = SocialNetworkVariant.GoodGraphEmptyNodes;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffGoodImpl_guessFollowsGraphReturnsNoEmptyNodes() {
        SocialNetwork.variant = SocialNetworkVariant.GoodGraphNoEmptyNodes;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }

    @Test public void yourTests_staffGoodImpl_guessFollowsGraphAlternatingCase() {
        SocialNetwork.variant = SocialNetworkVariant.GoodGraphAlternatingCase;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffGoodImpl_guessFollowsGraphBidirectionalFollowing() {
        SocialNetwork.variant = SocialNetworkVariant.GoodGraphReflexive;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffGoodImpl_influencersSameRankArbitraryOrder1() {
        SocialNetwork.variant = SocialNetworkVariant.GoodInfluencersHashcode;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffGoodImpl_influencersSameRankArbitraryOrder2() {
        SocialNetwork.variant = SocialNetworkVariant.GoodInfluencersReverseHashcode;
        assertSuccess(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_guessFollowsGraphCompleteGraphBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadGraphComplete;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_guessFollowsGraphSelfMentionBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadGraphSelfLoop;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_guessFollowsGraphMustBeCaseInsensitiveBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadGraphCaseSensitive;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_guessFollowsGraphAddsNonExistentUsersBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadGraphBadUsernames;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_guessFollowsGraphFollowsInWrongDirectionBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadGraphWrongDirection;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_influencersMustBeCaseInsensitiveBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadInfluencersCaseSensitive;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_influencersPrunesEmptyNodesBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadInfluencersPruneEmpty;
        assertFailure(runner.run(SocialNetworkTest.class));
    }
    
    @Test public void yourTests_staffBadImpl_influencersReturnsFirstInRankOnlyBug() {
        SocialNetwork.variant = SocialNetworkVariant.BadInfluencersFirstOnly;
        assertFailure(runner.run(SocialNetworkTest.class));
    }

//    @Test public void yourTests_staffBadImpl_influencersSortByRemovingFromInputBug() {
//        SocialNetwork.variant = SocialNetworkVariant.BadInfluencersSortByRemoving;
//        assertFailure(runner.run(SocialNetworkTest.class));
//    }
}
