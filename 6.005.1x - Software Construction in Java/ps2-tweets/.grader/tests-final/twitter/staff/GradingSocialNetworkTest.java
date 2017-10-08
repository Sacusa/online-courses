package twitter.staff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import twitter.SocialNetwork;
import twitter.Tweet;

public class GradingSocialNetworkTest {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    private static final Tweet tweet1 = new Tweet(0, "a_lyssp_", "no friends :(", Instant.now());
    private static final Tweet tweet2 = new Tweet(1, "a_lyssp_", "RT @a_lyssp_: no friends :(", Instant.now());
    private static final Tweet tweet3 = new Tweet(2, "bbitdiddle", "RT @a_lyssp_: no friends :(", Instant.now());
    private static final Tweet tweet4 = new Tweet(3, "evalu_", ".@a_lyssp_ how are you??", Instant.now());
    private static final Tweet tweet5 = new Tweet(4, "_reAsOnEr", "@evalu_ @a_lyssp_: great talk yesterday!", Instant.now());
    private static final Tweet tweet6 = new Tweet(5, "a_lyssp_", "@evalu_ doing great! you?", Instant.now());
    private static final Tweet tweet7 = new Tweet(88213, "123098", "123098123098", Instant.now());
    private static final Tweet tweet8 = new Tweet(79789783, "asdlkj", "asdlkjasdlkj", Instant.now());
    private static final Tweet tweet9 = new Tweet(9, "bbitdiddle", "@A_LYSSP_ hi hi hi", Instant.now());
    private static final Tweet tweet10 = new Tweet(10, "bbitdiddle", "@someoneElse lol", Instant.now());
    
    private static final List<Tweet> tweets = Collections.unmodifiableList(
            Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6, tweet7, tweet8, tweet9, tweet10));

    /**
     * Private tests for grading the SocialNetwork class.
     * 
     * Note that these tests are particularly sparse, due to the open-ended nature of the problem.
     */

    /**
     * Tests for SocialNetwork.guessFollowsGraph
     */
    
    @Test
    public void testGuessFollowsGraphEmptyList() {
        /*
         * No tweets -> no graph.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>());
        
        assertTrue("expected empty map", network.isEmpty());
    }
    
    @Test
    public void testGuessFollowsGraphUserMentionsSelf() {
        /*
         * Users should not be inferred as following themselves.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2));
        
        if (!network.isEmpty()) {
            assertEquals("map size", 1, network.keySet().size());
            for (String user : network.keySet()) {
                assertFalse("unexpected user following self", GradingUtilities.setToLowerCase(network.get(user)).contains(user.toLowerCase()));
            }
        }
    }
    
    @Test
    public void testGuessFollowsGraphSingleTweetAtMention() {
        /*
         * An at mention should result in a connection in the correct direction
         * for follows.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3));
        Map<String, Set<String>> canonicalNetwork = new HashMap<String, Set<String>>();

        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("a_lyssp_", "bbitdiddle"));
        Set<String> expectedKeyMap = new HashSet<String>(Arrays.asList("bbitdiddle"));

        canonicalNetwork.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        
        assertFalse("expected non-empty map", network.isEmpty());
        for (String user : network.keySet()) {
            if (canonicalNetwork.containsKey(user.toLowerCase())) {
                assertTrue("unexpected user(s) in map", canonicalUsers.contains(user.toLowerCase()));
                assertTrue("missing follow(s)", GradingUtilities.setToLowerCase(network.get(user)).containsAll(canonicalNetwork.get(user.toLowerCase())));
            }
        }

        assertTrue("missing key(s) in map", GradingUtilities.setToLowerCase(network.keySet()).containsAll(expectedKeyMap));
    }
    
    @Test
    public void testGuessFollowsGraphMustBeCaseInsensitive() {
        /*
         * Usernames should not be case-sensitive.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet9));
        Map<String, Set<String>> canonicalNetwork = new HashMap<String, Set<String>>();
        
        canonicalNetwork.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        assertFalse("expected non-empty map", network.isEmpty());
        assertTrue("expected case-insensitive usernames", GradingUtilities.setIsCaseInsensitiveUnique(network.keySet()));
        for (String user : network.keySet()) {
            assertTrue("expected case-insensitive usernames", GradingUtilities.setIsCaseInsensitiveUnique(network.get(user)));
            if (canonicalNetwork.containsKey(user.toLowerCase())) {
                assertTrue("missing follow(s)", GradingUtilities.setToLowerCase(network.get(user)).containsAll(canonicalNetwork.get(user.toLowerCase())));
            }
        }
        
    }
    
    @Test
    public void testGuessFollowsGraphHasConnections() {
        /*
         * Ensuring that a larger graph captures the bare minimum of @-follows.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6));
        Map<String, Set<String>> canonicalNetwork = new HashMap<String, Set<String>>();
        
        canonicalNetwork.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        canonicalNetwork.put("evalu_", new HashSet<String>(Arrays.asList("a_lyssp_")));
        canonicalNetwork.put("a_lyssp_", new HashSet<String>(Arrays.asList("evalu_")));
        canonicalNetwork.put("_reasoner", new HashSet<String>(Arrays.asList("a_lyssp_", "evalu_")));
        
        assertFalse("expected non-empty map", network.isEmpty());
        for (String user : network.keySet()) {
            assertTrue("missing follow(s)", GradingUtilities.setToLowerCase(network.get(user)).containsAll(canonicalNetwork.get(user.toLowerCase())));
        }
    }
    
    @Test
    public void testGuessFollowsGraphNoConnections() {
        /*
         * Ensures that tweets that cannot possibly be related are not related.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7, tweet8));
        
        if (!network.isEmpty()) {
            for (String user : network.keySet()) {
                assertTrue("expected no follows", network.get(user).isEmpty());
            }
        }
    }
    
    @Test
    public void testGuessFollowsGraphMustHaveNoNonexistentUsers() {
        /*
         * Ensuring that a test on a large graph only maps to users that actually exist within
         * the tweet-verse.
         */
        Map<String, Set<String>> network = SocialNetwork.guessFollowsGraph(tweets);
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("a_lyssp_", "bbitdiddle",
                "evalu_", "_reasoner", "123098", "asdlkj", "someoneelse"));
        Set<String> includedUsers = new HashSet<String>();
        
        includedUsers.addAll(network.keySet());
        
        for (String user: network.keySet()) {
            includedUsers.addAll(network.get(user));
        }
        
        assertTrue("unexpected user(s) in map", canonicalUsers.containsAll(GradingUtilities.setToLowerCase(includedUsers)));
        
    }
    
    /**
     * Test for SocialNetwork.influencers
     */
    
    @Test
    public void testInfluencersEmptyGraph() {
        /*
         * No tweets -> no ranking
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    @Test
    public void testInfluencersGraphOneUserNoFollowing() {
        /*
         * Single tweet: should be length 1, have only that user. Note: even if they have
         * no influence, they should still be in the list, as the spec calls for all users that
         * are in the network.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("a_lyssp_", new HashSet<String>());

        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 1, influencers.size());
        assertEquals("incorrect user", "a_lyssp_", influencers.get(0).toLowerCase());
    }
    
    @Test
    public void testInfluencersTwoUsersOneFollowing() {
        /*
         * Single tweet with one mention: should be length one, with the person who has been
         * mentioned ranking higher than the other person.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 2, influencers.size());
        assertTrue("incorrect user at index 0", influencers.get(0).toLowerCase().equals("a_lyssp_"));
        assertTrue("incorrect user at index 1", influencers.get(1).toLowerCase().equals("bbitdiddle"));
    }
    
    @Test
    public void testInfluencersMustBeCaseInsensitive() {
        /*
         * Usernames should be case insensitive.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("bbitdiddle", new HashSet<String>(Arrays.asList("a_lyssp_")));
        network.put("A_LYSSP_", new HashSet<String>());
        
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 2, influencers.size());
        assertTrue("incorrect user at index 0", influencers.get(0).toLowerCase().equals("a_lyssp_"));
        assertTrue("incorrect user at index 1", influencers.get(1).toLowerCase().equals("bbitdiddle"));
    }
    
    @Test
    public void testInfluencersMultipleUsersAllDifferentFollowerCounts() {
        /*
         * Testing multiple users with clear rankings.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("a_lyssp_", new HashSet<String>(Arrays.asList("evalu_", "alice")));
        network.put("evalu_", new HashSet<String>(Arrays.asList("a_lyssp_", "alice")));
        network.put("_reasoner", new HashSet<String>(Arrays.asList("a_lyssp_", "alice")));
        
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 4, influencers.size());
        assertTrue("incorrect user at index 0", influencers.get(0).toLowerCase().equals("alice"));
        assertTrue("incorrect user at index 1", influencers.get(1).toLowerCase().equals("a_lyssp_"));
        assertTrue("incorrect user at index 2", influencers.get(2).toLowerCase().equals("evalu_"));
        assertTrue("incorrect user at index 3", influencers.get(3).toLowerCase().equals("_reasoner"));
    }
    
    @Test
    public void testInfluencersMultipleUsersAllSameFollowerCounts() {
        /*
         * Testing multiple users who all tie. The program should not panic and should
         * return all of the users in some implementer-dictated order.
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("a_lyssp_", new HashSet<String>());
        network.put("bbitdiddle", new HashSet<String>());
        network.put("alice", new HashSet<String>());
        network.put("bob", new HashSet<String>());
        
        List<String> canonicalUsers = Arrays.asList("a_lyssp_", "bbitdiddle", "alice", "bob");
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 4, influencers.size());
        for (String user: influencers) {
            assertTrue("missing expected user", canonicalUsers.contains(user.toLowerCase()));
        }
        
    }
    
    @Test
    public void testInfluencersMultipleUsersSomeSameFollowerCounts() {
        /*
         * 
         */
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        network.put("a_lyssp_", new HashSet<String>());
        network.put("bbitdiddle", new HashSet<String>());
        network.put("alice", new HashSet<String>(Arrays.asList("bob", "a_lyssp_")));
        network.put("bob", new HashSet<String>(Arrays.asList("alice", "a_lyssp_")));
        
        List<String> canonicalUsers = Arrays.asList("a_lyssp_", "bbitdiddle", "alice", "bob");
        List<String> influencers = SocialNetwork.influencers(network);
        
        assertFalse("expected non-empty list", influencers.isEmpty());
        assertEquals("list size", 4, influencers.size());
        assertTrue("incorrect user at index 0", influencers.get(0).toLowerCase().equals("a_lyssp_"));
        assertTrue(influencers.get(influencers.size() - 1).toLowerCase().equals("bbitdiddle"));
        for (String user: influencers) {
            assertTrue("missing expected user", canonicalUsers.contains(user.toLowerCase()));
        }
    }

}
