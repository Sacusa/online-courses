package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class MySocialNetworkTest {
    
    /**
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
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much? #mit", Instant.parse("2016-02-17T10:00:00Z"));
        tweets.add(tweet1);
        map.put("alyssa", new HashSet<String>());

        assertEquals(map, SocialNetwork.guessFollowsGraph(tweets));
    }

    // covers tweets.size() = 5 (> 1)
    @Test
    public void followsTweetsMany() {        
        List<Tweet> tweets = new ArrayList<Tweet>();
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();

        Instant d1 = Instant.parse("2016-02-17T10:00:00Z");

        Tweet tweet1 = new Tweet(1, "alyssa", "foo #t1 #t2 #t3 bar #t11 oh #t12", d1);
        Tweet tweet2 = new Tweet(2, "tyrell", "foo #t6 #t7 #t8 bar #t9 yeah #t11", d1);
        Tweet tweet3 = new Tweet(3, "elliott", "foo #t6 #t7 #t8 bar #t10 man #t12", d1);
        Tweet tweet4 = new Tweet(4, "darlene", "foo #t1 #t2 #t3 bar #t4 #t5", d1);
        Tweet tweet5 = new Tweet(5, "price", "i'm all #alone #sad", d1);

        tweets.add(tweet1);
        tweets.add(tweet2);
        tweets.add(tweet3);
        tweets.add(tweet4);
        tweets.add(tweet5);

        // add entry for alyssa
        Set<String> alyssaFollows = new HashSet<String>();
        alyssaFollows.add("tyrell");
        alyssaFollows.add("elliott");
        alyssaFollows.add("darlene");
        map.put("alyssa", alyssaFollows);

        // add entry for tyrell
        Set<String> tyrellFollows = new HashSet<String>();
        tyrellFollows.add("alyssa");
        tyrellFollows.add("elliott");
        map.put("tyrell", tyrellFollows);

        // add entry for elliott
        Set<String> elliottFollows = new HashSet<String>();
        elliottFollows.add("alyssa");
        elliottFollows.add("tyrell");
        map.put("elliott", elliottFollows);

        // add entry for darlene
        Set<String> darleneFollows = new HashSet<String>();
        darleneFollows.add("alyssa");
        map.put("darlene", darleneFollows);
        
        // add entry for price
        map.put("price", new HashSet<String>());

        assertEquals(map, SocialNetwork.guessFollowsGraph(tweets));
    }
}
