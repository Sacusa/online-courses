package twitter.staff;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import twitter.*;

public class GradingExtractTest {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
        
    // Fri 14 Feb 2015 01:18:18 PM EST
    private static final Instant d1 = Instant.ofEpochMilli(1423937898000L);
    // Sat 15 Feb 2015 01:18:18 PM EST
    private static final Instant d2 = Instant.ofEpochMilli(1424024298000L);
    // Sun 16 Feb 2015 11:18:18 AM EST
    private static final Instant d3 = Instant.ofEpochMilli(1424103498000L);

    private static final Tweet tweet1 = GradingUtilities.createTweetWithDate(d1);
    private static final Tweet tweet2 = GradingUtilities.createTweetWithDate(d1);
    private static final Tweet tweet3 = GradingUtilities.createTweetWithDate(d2);
    private static final Tweet tweet4 = GradingUtilities.createTweetWithDate(d2);
    private static final Tweet tweet5 = GradingUtilities.createTweetWithDate(d3);

    /**
     * Private tests for grading the Extract class.
     */

    /**
     * Tests for Extract.getTimespan
     */
    @Test
    public void testGetTimespanEmptyListAlwaysReturnsZeroLengthTimespan() {
        /*
         * The timespan of an empty list should be zero length. We don't care
         * what time is put in there.
         */
        Timespan timespan = Extract.getTimespan(new ArrayList<Tweet>());
        if (timespan.getStart() == timespan.getEnd()) {
            return;
        }
        
        long startTime = System.nanoTime();
        long durationToRunTestNanoSeconds = 100_000_000; // 100 milliseconds
        while ((System.nanoTime()) - startTime <= durationToRunTestNanoSeconds ) {
            timespan = Extract.getTimespan(new ArrayList<Tweet>());
            if (!timespan.getStart().equals(timespan.getEnd())){
                fail();
            }
        }
    }

    @Test
    public void testGetTimespanSingleTweet() {
        /*
         * The timespan of a list containing a single tweet should also have
         * zero length. However, the timespan should be directly on the
         * timestamp of the tweet.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
    }

    @Test
    public void testGetTimespanTwoDuplicateStamps() {
        /*
         * This test is identical to the case for a single tweet. We want the
         * resultant timespan to be the minimal covering timespan.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        assertEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
    }

    @Test
    public void testGetTimespanTwoDifferentStamps() {
        /*
         * This test checks to make sure that when we give them two tweets, that
         * the timespan correctly handles setting the start and end.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet3.getTimestamp(), timespan.getEnd());

        // Make sure the solution isn't order dependant.
        timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet1));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet3.getTimestamp(), timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleStampsWithDuplicates() {
        /*
         * Testing to make sure duplicates are handled as well as more than two
         * tweets as input.
         */

        // Testing duplicates at the start of the timespan.
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2,
                tweet3));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet3.getTimestamp(), timespan.getEnd());

        // Testing duplicates at the end of the timespan.
        timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3, tweet4));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet3.getTimestamp(), timespan.getEnd());
    }

    @Test
    public void testGetTimespanMultipleStampsWithoutDuplicates() {
        /*
         * Testing to make sure more than two inputs are handled.
         */
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet3,
                tweet5));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet5.getTimestamp(), timespan.getEnd());

        // One last check on order.
        timespan = Extract.getTimespan(Arrays.asList(tweet3, tweet5, tweet1));
        assertNotEquals(timespan.getStart(), timespan.getEnd());
        assertEquals(tweet1.getTimestamp(), timespan.getStart());
        assertEquals(tweet5.getTimestamp(), timespan.getEnd());
    }

    /**
     * Tests for Extract.getMentionedUsers
     */

    @Test
    public void testGetMentionedUsersEmptyList() {
        /*
         * An empty list of tweets should not have any mentions.
         */
        Set<String> mentionedUsers = Extract
                .getMentionedUsers(new ArrayList<Tweet>());

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersNoMentionsSingleTweet() {
        /*
         * If a tweet doesn't have any mentions, none should be returned.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities.createTweetWithText("noMention")));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleMentionSingleTweet() {
        /*
         * Testing a single mention in a single tweet.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities.createTweetWithText("following text will be @mention")));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        for (String user : mentionedUsers) {
            assertEquals("incorrect user", "mention", user.toLowerCase());
        }
    }

    @Test
    public void testGetMentionedUsersSingleMentionMultipleTweets() {
        /*
         * Testing a single mention across multiple tweets.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText(". @mention"),
                GradingUtilities.createTweetWithText("@mention ."),
                GradingUtilities.createTweetWithText("@mention")));
        Set<String> canonicalUsers = new HashSet<String>(
                Arrays.asList("mention"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersSingleMentionMultipleTweetsMustBeCaseInsensitive() {
        /*
         * Testing a single mention across multiple tweets, with different
         * cases. They should be doing something to prevent different cases from
         * causing an issue in the set.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText("@mention"),
                GradingUtilities.createTweetWithText("@MeNtIoN")));
        Set<String> canonicalUsers = new HashSet<String>(
                Arrays.asList("mention"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 1, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsSingleTweet() {
        /*
         * Testing multiple mentions in a single tweet.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities
                        .createTweetWithText("@mention @othermention")));
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList(
                "mention", "othermention"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 2, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersMultipleMentionsMultipleTweets() {
        /*
         * Testing multiple mentions across multiple tweets.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText("@mention @othermention"),
                GradingUtilities.createTweetWithText("@thirdMention")));
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList(
                "mention", "othermention", "thirdmention"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 3, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersEmailAddress() {
        /*
         * Testing to ensure emails are not grabbed by mistake.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities.createTweetWithText("test@dev.null")));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersSingleAtSignNoFollowingUsername() {
        /*
         * Testing to ensure that single @ characters are not grabbed.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities.createTweetWithText("@")));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    @Test
    public void testGetMentionedUsersInvalidFollowingCharacters() {
        /*
         * Testing to ensure that invalid following characters are properly ignored.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText("@\u2026"),
                GradingUtilities.createTweetWithText("@!!!")));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
        
    }

    @Test
    public void testGetMentionedUsersMultipleAtSigns() {
        /*
         * Testing to ensure nothing strange is returned when there is a user
         * mention preceded by multiple @ signs.
         */

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays
                .asList(GradingUtilities.createTweetWithText("@@@ben")));
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("ben"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 1, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }
    
    @Test
    public void testGetMentionedUsersUsernameIsHyphenated() {
        /*
         * Testing to ensure that user mentions allow hyphens
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText("The one and only @the-best is the best!")));
        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertTrue("wrong number of users found", mentionedUsers.size() == 1);
        assertTrue("unexpected user in set", mentionedUsers.iterator().next().toLowerCase().equals("the-best"));
    }

    @Test
    public void testGetMentionedUsersPreceededByPunctuation() {
        /*
         * Testing to ensure that user mentions preceeded by punctuation are
         * caught correctly.
         */

        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText(".@ben bitdiddle~"),
                GradingUtilities.createTweetWithText("I !!@will survive!")));
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("ben", "will"));
        
        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 2, mentionedUsers.size());
        for (String user: mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersUnderscores() {
        /*
         * Ensuring that underscores are correctly handled.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
            GradingUtilities.createTweetWithText("_ @_test_ _"),
            GradingUtilities.createTweetWithText("this is a @te_st")));
        Set<String> canonicalUsers = new HashSet<String>(
                Arrays.asList("_test_", "te_st"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 2, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }

    @Test
    public void testGetMentionedUsersUsernameEndedByPunctuation() {
        /*
         * Ensuring usernames are properly delimited.
         */
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(
                GradingUtilities.createTweetWithText("dummyText @test:!"),
                GradingUtilities.createTweetWithText("@test2's dummyText")));
        Set<String> canonicalUsers = new HashSet<String>(Arrays.asList("test",
                "test2"));

        assertFalse("expected non-empty set", mentionedUsers.isEmpty());
        assertEquals("set size", 2, mentionedUsers.size());
        for (String user : mentionedUsers) {
            assertTrue("unexpected user in set", canonicalUsers.contains(user.toLowerCase()));
        }
    }
}
