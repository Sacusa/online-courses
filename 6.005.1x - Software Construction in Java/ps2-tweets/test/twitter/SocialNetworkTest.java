package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    /*
     * Testing strategy for SocialNetwork.guessFollowsGraph()
     * 
     * Partition the inputs as follows:
     * tweets.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */
    
    // covers tweets.size() = 0
    @Test
    public void followsTweetsZero() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        assertEquals(map, SocialNetwork.guessFollowsGraph(tweets));
    }
    
    // covers tweets.size() = 1
    @Test
    public void followsTweetsOne() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Map<String, Set<String>> answer1 = new HashMap<String, Set<String>>();
        Map<String, Set<String>> answer2 = new HashMap<String, Set<String>>();
        
        Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
        Tweet tweet1 = new Tweet(1, "alySsa", "is it reasonable to talk about rivest so much?", d1);
        
        tweets.add(tweet1);
        answer1.put("alyssa", new HashSet<String>());
        
        Map<String, Set<String>> map = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> mapLowerCase = new HashMap<String, Set<String>>();
        Set<String> mapKeys = map.keySet();
        
        for (String key : mapKeys) {
            Set<String> newValues = new HashSet<String>();
            
            for (String value : map.get(key)) {
                String valueLowerCase = value.toLowerCase();
                
                // check for duplicate entry
                if (newValues.contains(valueLowerCase)) {
                    fail("CaseInsensitiveBug");
                }
                
                newValues.add(valueLowerCase);
            }
            
            String keyLowerCase = key.toLowerCase();
            
            // check for duplicate key
            if (mapLowerCase.containsKey(keyLowerCase)) {
                fail("CaseInsensitiveBug");
            }
            
            mapLowerCase.put(keyLowerCase, newValues);
        }
        
        assertTrue(mapLowerCase.equals(answer1) || mapLowerCase.equals(answer2));
    }
    
    // covers tweets.size() = 5 (> 1)
    @Test
    public void followsTweetsMany() {
        List<Tweet> tweets = new ArrayList<Tweet>();
        Map<String, Set<String>> answer1 = new HashMap<String, Set<String>>();
        Map<String, Set<String>> answer2 = new HashMap<String, Set<String>>();
        boolean meetsRequirements = true;
        
        Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
        
        Tweet tweet1 = new Tweet(1, "AlYsSa", "@ellIott , @darlene , is it reasonable @TyReLl ?", d1);
        Tweet tweet2 = new Tweet(1, "tYrElL", "@elLioTt @tyrelL , is it reasonable @price ?", d1);
        Tweet tweet3 = new Tweet(1, "ElLiOtT", "is it reasonable to talk @dArlene about rivest so much?", d1);
        Tweet tweet4 = new Tweet(1, "dArLeNe", "is it reasonable to talk about rivest so much?", d1);
        Tweet tweet5 = new Tweet(1, "PrIcE", "is it reasonable to talk about rivest so much?", d1);
        
        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);
        
        // add entry for alyssa
        Set<String> alyssaFollows = new HashSet<String>();
        alyssaFollows.add("elliott");
        alyssaFollows.add("darlene");
        alyssaFollows.add("tyrell");
        answer1.put("alyssa", alyssaFollows);
        answer2.put("alyssa", alyssaFollows);
        
        // add entry for tyrell
        Set<String> tyrellFollows = new HashSet<String>();
        tyrellFollows.add("elliott");
        tyrellFollows.add("price");
        answer1.put("tyrell", tyrellFollows);
        answer2.put("tyrell", tyrellFollows);
        
        // add entry for elliott
        Set<String> elliottFollows = new HashSet<String>();
        elliottFollows.add("darlene");
        answer1.put("elliott", elliottFollows);
        answer2.put("elliott", elliottFollows);
        
        // add entry for darlene
        Set<String> darleneFollows = new HashSet<String>();
        answer1.put("darlene", darleneFollows);
        
        // add entry for price
        Set<String> priceFollows = new HashSet<String>();
        answer1.put("price", priceFollows);
        
        Map<String, Set<String>> map = SocialNetwork.guessFollowsGraph(tweets);
        Map<String, Set<String>> mapLowerCase = new HashMap<String, Set<String>>();
        Set<String> mapKeys = map.keySet();
        
        for (String key : mapKeys) {
            Set<String> newValues = new HashSet<String>();
            String keyLowerCase = key.toLowerCase();
            
            for (String value : map.get(key)) {
                String valueLowerCase = value.toLowerCase();
                
                // check for duplicate entry
                if (newValues.contains(valueLowerCase)) {
                    fail("CaseInsensitiveBug");
                }
                
                // check if key = some value
                if (keyLowerCase.equals(valueLowerCase)) {
                    fail("SelfMentionBug");
                }
                
                newValues.add(valueLowerCase);
            }
            
            // check for duplicate key
            if (mapLowerCase.containsKey(keyLowerCase)) {
                fail("CaseInsensitiveBug");
            }
            
            // check minimum requirements
            if (!(newValues.containsAll(answer1.get(keyLowerCase)) || newValues.containsAll(answer2.get(keyLowerCase)))) {
                meetsRequirements = false;
            }
            
            mapLowerCase.put(keyLowerCase, newValues);
        }
        
        // assertTrue(mapLowerCase.equals(answer1) || mapLowerCase.equals(answer2));
        assertTrue(meetsRequirements);
    }
    
    /*
     * Testing strategy for SocialNetwork.influencers()
     * 
     * Partition the inputs as follows:
     * map.size() 0, 1, > 1
     * 
     * Exhaustive Cartesian coverage of partitions.
     */
    
    // covers map.size() = 0
    @Test
    public void influencersMapZero() {
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        List<String> influencers = new ArrayList<String>();
        
        assertEquals(influencers, SocialNetwork.influencers(followsGraph));
    }
    
    // covers map.size() = 1
    @Test
    public void influencersMapOne() {
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        List<String> answer = new ArrayList<String>();
        
        followsGraph.put("alyssa", new HashSet<String>());
        answer.add("alyssa");
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        String [] influencersArray = influencers.toArray(new String[0]);
        influencers = new ArrayList<String>();
        
        for (int i = 0; i < influencersArray.length; ++i) {
            String influencer = influencersArray[i].toLowerCase();
            
            // check for duplicate entry
            if (influencers.contains(influencer)) {
                fail("CaseInsensitiveBug");
            }
            
            influencers.add(influencer);
        }
        
        assertEquals(answer, influencers);
    }
    
    // covers map.size() = 5 (> 1)
    @Test
    public void influencersMapMany() {
        Map<String, Set<String>> followsGraph = new HashMap<String, Set<String>>();
        List<String> answer1 = new ArrayList<String>();
        List<String> answer2 = new ArrayList<String>();
        
        // map entry 1
        Set<String> followedUsers1 = new HashSet<String>();
        followsGraph.put("Price", followedUsers1);
        
        // map entry 2
        Set<String> followedUsers2 = new HashSet<String>();
        followedUsers2.add("price");
        followsGraph.put("Elliott", followedUsers2);
        
        // map entry 3
        Set<String> followedUsers3 = new HashSet<String>();
        followedUsers3.add("Price");
        followedUsers3.add("elliott");
        followsGraph.put("tyrell", followedUsers3);
        
        // map entry 4
        Set<String> followedUsers4 = new HashSet<String>();
        followedUsers4.add("price");
        followedUsers4.add("elliott");
        followedUsers4.add("tyrell");
        followsGraph.put("darlene", followedUsers4);
        
        // map entry 5
        Set<String> followedUsers5 = new HashSet<String>();
        followedUsers5.add("price");
        followedUsers5.add("elliott");
        followedUsers5.add("tyrell");
        followsGraph.put("whiterose", followedUsers5);
        
        // set the value of influencers
        answer1.add("price");
        answer1.add("elliott");
        answer1.add("tyrell");
        answer1.add("darlene");
        answer1.add("whiterose");
        
        answer2.add("price");
        answer2.add("elliott");
        answer2.add("tyrell");
        answer2.add("whiterose");
        answer2.add("darlene");
        
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        String [] influencersArray = influencers.toArray(new String[0]);
        influencers = new ArrayList<String>();
        
        for (int i = 0; i < influencersArray.length; ++i) {
            String influencer = influencersArray[i].toLowerCase();
            
            // check for duplicate entry
            if (influencers.contains(influencer)) {
                fail("CaseInsensitiveBug");
            }
            
            influencers.add(influencer);
        }
        
        assertTrue(influencers.equals(answer1) || influencers.equals(answer2));
    }
    
    /*
     * END OF MY TESTS
     */

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
