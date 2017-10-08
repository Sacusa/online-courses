package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import twitter.Timespan;
import twitter.Tweet;

public class Filter {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    public enum FilterVariant {
        Good, BadWrittenByFirstResult, BadWrittenByFullList, BadWrittenByCaseSensitive,
        BadWrittenByIOBX, //BadWrittenByDuplicateTweet,
        BadInTimespanFullList,
        BadInTimespanNonInclusive, BadInTimespanOrder, //BadInTimespanMutateOriginal,
        BadContainingFullList, BadContainingCaseSensitive, BadContainingAndNotOr,
        BadContainingSubstring //, BadContainingRepeatingWords
    }

    public static FilterVariant variant = FilterVariant.Good;

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        switch (variant) {
        case Good: return writtenBy_Good(tweets, username);
        case BadWrittenByFirstResult: return writtenBy_BadFirstResult(tweets, username);
        case BadWrittenByFullList: return writtenBy_BadFullList(tweets, username);
        case BadWrittenByCaseSensitive: return writtenBy_BadCaseSensitive(tweets, username);
        case BadWrittenByIOBX: return writtenBy_BadIOBX(tweets, username);
//        case BadWrittenByDuplicateTweet: return writtenBy_BadDuplicateTweet(tweets, username);
        default: return writtenBy_Good(tweets, username);
        }
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        switch (variant) {
        case Good: return inTimespan_Good(tweets, timespan);
        case BadInTimespanFullList: return inTimespan_BadFullList(tweets, timespan);
        case BadInTimespanNonInclusive: return inTimespan_BadNonInclusive(tweets, timespan);
        case BadInTimespanOrder: return inTimespan_BadOrder(tweets, timespan);
        // case BadInTimespanMutateOriginal: return inTimespan_BadMutateOriginal(tweets, timespan);
        default: return inTimespan_Good(tweets, timespan);
        }
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        switch (variant) {
        case Good: return containing_Good(tweets, words);
        case BadContainingFullList: return containing_BadFullList(tweets, words);
        case BadContainingCaseSensitive: return containing_BadCaseSensitive(tweets, words);
        case BadContainingAndNotOr: return containing_BadAndNotOr(tweets, words);
        case BadContainingSubstring: return containing_BadSubstring(tweets, words);
//        case BadContainingRepeatingWords: return containing_BadRepeatingWords(tweets, words);
        default: return containing_Good(tweets, words);
        }
    }
    
    /*************************
     * Good Implementations **
     */
    
    private static List<Tweet> writtenBy_Good(List<Tweet> tweets, String username) {
        List<Tweet> results = new ArrayList<Tweet>();
        for (Tweet t : tweets) {
            if (t.getAuthor().toLowerCase().equals(username.toLowerCase())) {
                results.add(t);
            }
        }
        return results;
    }
    
    private static List<Tweet> inTimespan_Good(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> results = new ArrayList<Tweet>();
        for (Tweet t : tweets) {
            if ((t.getTimestamp().isAfter(timespan.getStart()) && 
                    t.getTimestamp().isBefore(timespan.getEnd())) ||
                    t.getTimestamp().equals(timespan.getStart()) ||
                    t.getTimestamp().equals(timespan.getEnd())) {
                results.add(t);
            }
        }
        return results;
    }
    
    private static List<Tweet> containing_Good(List<Tweet> tweets, List<String> words) {
        List<Tweet> rt = new ArrayList<Tweet>();
        for (Tweet t: tweets) {
            for (String w: words) {
                if (Arrays.asList(t.getText().toLowerCase().split(" ")).contains(w.toLowerCase()) && !rt.contains(t)) {
                    rt.add(t);
                }
            }
        }
        return rt;
    }
    
    /************************
     * Bad Implementations **
     */
    
    /**
     * writtenBy()
     */
    
    
    private static List<Tweet> writtenBy_BadFirstResult(List<Tweet> tweets, String username) {
        // A trap just for those who only test using one tweet. Too cruel?
        for (Tweet t: tweets) {
            if (t.getAuthor().toLowerCase().equals(username.toLowerCase())) {
                return Arrays.asList(t);
            }
        }
        return new ArrayList<Tweet>();
    }
    
    private static List<Tweet> writtenBy_BadFullList(List<Tweet> tweets, String username) {
        // don't test by making all the authors the same!
        return tweets;
    }
    
    private static List<Tweet> writtenBy_BadCaseSensitive(List<Tweet> tweets, String username) {
        // doesn't do case-insensitive querying
        List<Tweet> rt = new ArrayList<Tweet>();
        for (Tweet t: tweets) {
            if (t.getAuthor().equals(username)) {
                rt.add(t);
            }
        }
        return rt;
    }
    
    private static List<Tweet> writtenBy_BadIOBX(List<Tweet> tweets, String username) {
        if (tweets.get(0).getAuthor().equals("")) {
            return new ArrayList<Tweet>();
        }
        List<Tweet> results = new ArrayList<Tweet>();
        for (Tweet t : tweets) {
            if (t.getAuthor().toLowerCase().equals(username.toLowerCase())) {
                results.add(t);
            }
        }
        return results;
    }
//    NOT ALLOWED WITH UNIQUE TWEETS ANYMORE
//    private static List<Tweet> writtenBy_BadDuplicateTweet(List<Tweet> tweets, String username) {
//        List<Tweet> results = new ArrayList<Tweet>();
//        for (Tweet t : tweets) {
//            if (t.getAuthor().toLowerCase().equals(username.toLowerCase())
//                    && !results.contains(t)) {
//                results.add(t);
//            }
//        }
//        return results;
//    }

    /**
     * inTimespan()
     */
    
    private static List<Tweet> inTimespan_BadFullList(List<Tweet> tweets, Timespan timespan) {
        // another trap! Don't test using the entire timespan, kids.
        return tweets;
    }
    
    private static List<Tweet> inTimespan_BadNonInclusive(List<Tweet> tweets, Timespan timespan) {
        // non-inclusive return
        List<Tweet> rt = new ArrayList<Tweet>();
        for (Tweet t: tweets) {
            if (t.getTimestamp().isAfter(timespan.getStart()) && t.getTimestamp().isBefore(timespan.getEnd())) {
                rt.add(t);
            }
        }
        return rt;
    }
    
    private static List<Tweet> inTimespan_BadOrder(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> results = new ArrayList<Tweet>();
        boolean timespanStarted = false;
        boolean timespanEnded = false;
        for (Tweet t : tweets) {
            if (!timespanEnded && t.getTimestamp().isAfter(timespan.getStart())) {
                timespanStarted = true;
                results.add(t);
            } else if (timespanStarted && !timespanEnded && t.getTimestamp().isBefore(timespan.getEnd())) {
                results.add(t);
            } else if (timespanStarted && !timespanEnded && t.getTimestamp().isAfter(timespan.getEnd())) {
                timespanEnded = true;
            } else {
                // shouldn't be here?
                continue;
            }
        }
        return results;
    }
    
    private static List<Tweet> inTimespan_BadMutateOriginal(List<Tweet> tweets, Timespan timespan) {
        for (Iterator<Tweet> it = tweets.iterator(); it.hasNext(); ) {
            Tweet t = it.next();
            // remove the tweet if its timespan is not in the range
            if (t.getTimestamp().isBefore(timespan.getStart()) || t.getTimestamp().isAfter(timespan.getEnd())) {
                it.remove();
            }
        }
        return tweets;
    }

    /**
     * containing()
     */
    
    private static List<Tweet> containing_BadFullList(List<Tweet> tweets, List<String> words) {
        return tweets;
    }
    
    private static List<Tweet> containing_BadCaseSensitive(List<Tweet> tweets, List<String> words) {
        /*
         * case-sensitive, all words
         */
        List<Tweet> rt = new ArrayList<Tweet>();
        for (Tweet t: tweets) {
            for (String w: words) {
                if (t.getText().contains(w) && !rt.contains(t)) {
                    rt.add(t);
                }
            }
        }
        return rt;
    }
    
    private static List<Tweet> containing_BadAndNotOr(List<Tweet> tweets, List<String> words) {
        /*
         * case-insensitive, all words
         */
        List<Tweet> results = new ArrayList<Tweet>();
        for (Tweet t : tweets) {
            if (stringListToLower(Arrays.asList(t.getText().split("\\s+"))).containsAll(stringListToLower(words))){
                results.add(t);
            }
        }
        return results;
    }
    
    private static List<Tweet> containing_BadSubstring(List<Tweet> tweets, List<String> words) {
        List<Tweet> results = new ArrayList<Tweet>();
        for (Tweet t : tweets) {
            boolean containsOneWord = false;
            for (String word : words) {
                if (t.getText().toLowerCase().contains(word.toLowerCase())) {
                    containsOneWord = true;
                    break;
                }
            }
            
            if (containsOneWord) {
                results.add(t);
            }
        }
        return results;
    }
    
    // Doesn't make sense with an OR implementation of containing
//    private static List<Tweet> containing_BadRepeatingWords(List<Tweet> tweets, List<String> words) {
//        List<Tweet> results = new ArrayList<Tweet>();
//        for (Tweet t : tweets) {
//            List<String> tweetWords = new ArrayList<String>(
//                    stringListToLower(Arrays.asList(t.getText().split("\\s+"))));
//            boolean ok = true;
//            for (String word : words) {
//                if (!tweetWords.remove(word)) {
//                    ok = false;
//                }
//            }
//            if (ok){
//                results.add(t);
//            }
//        }
//        return results;
//    }

    /*********************
     * Helper Functions **
     */

    private static List<String> stringListToLower(List<String> inputs) {
        List<String> outputs = new ArrayList<String>();
        for (String s : inputs) {
            outputs.add(s.toLowerCase());
        }
        return outputs;
    }
    
}
