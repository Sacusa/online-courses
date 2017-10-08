package twitter;

import java.time.Instant;
import java.util.*;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        Timespan minimumTimespan;
        Instant minimumTimestamp, maximumTimestamp;
        
        // get initial maximum timestamp
        if (tweets.size() >= 1) {
            maximumTimestamp = minimumTimestamp = tweets.get(0).getTimestamp();
        }
        else {
            Instant date = Instant.now();
            minimumTimespan = new Timespan(date, date);
            return minimumTimespan;
        }
        
        // set the maximumTimestamp to the maximum timestamp value of all tweets
        for (Tweet tweet : tweets) {
            Instant nextTimestamp = tweet.getTimestamp();
            
            // check if nextTimestamp is > maximumTimestamp
            if (maximumTimestamp.compareTo(nextTimestamp) < 0) {
                maximumTimestamp = nextTimestamp;
            }
            
            // check if nextTimestamp is < minimumTimestamp
            if (minimumTimestamp.compareTo(nextTimestamp) > 0) {
                minimumTimestamp = nextTimestamp;
            }
        }
        
        // set the minimumTimespan to the required value
        minimumTimespan = new Timespan(minimumTimestamp, maximumTimestamp);
        
        return minimumTimespan;
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> users = new HashSet<String>();
        
        // check if list of tweets is empty
        if (tweets.size() == 0) {
            return users;
        }
        
        for (Tweet tweet : tweets) {
            
            String tweetText = tweet.getText();
            
            // divide the tweet text by @
            String [] partitions = tweetText.split("@");
            
            // no @ in the string
            if (partitions.length <= 1) {
                return users;
            }
            
            for (int i = 1; i < partitions.length; ++i) {
                
                // check the current parition's length
                if (partitions[i].length() == 0) {
                    continue;
                }
                
                // ensure that the @ sign is not preceeded by a valid username char
                if (partitions[i - 1].length() > 0) {
                    if (isValidUserChar(partitions[i - 1].charAt(partitions[i - 1].length() - 1))) {
                        continue;
                    }
                }
                    
                // break the partition into a char array
                char [] lettersInPartition = partitions[i].toCharArray();

                // add the username into a new StringBuilder
                StringBuilder lettersInUsername = new StringBuilder();
                boolean addUser = false;

                for (char letter : lettersInPartition) {
                    if (isValidUserChar(letter)) {
                        lettersInUsername.append(letter);
                        addUser = true;
                    }
                    else {
                        break;
                    }
                }
                
                if (addUser) {
                    users.add(lettersInUsername.toString().toLowerCase());
                }
            }
        }
        
        return users;
    }
    
    /*
     * Check if letter is a valid letter in a Twitter username,
     * as specified by Tweet.getAuthor() spec.
     * 
     * @param letter
     *          the letter to be checked
     * 
     * @return true if the letter is valid, else false
     */
    private static boolean isValidUserChar(char letter) {        
        if ((letter >= 'A' && letter <= 'Z') || (letter >= 'a' && letter <= 'z') || (letter >= '0' && letter <= '9') || (letter == '_') || (letter == '-')) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get hashtags mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * 
     * @return a map from tweet author to a set of strings, containing the hashtags
     *         mentioned in the tweet of the user.
     */
    public static Map<String, Set<String>> getHashtags(List<Tweet> tweets) {
        Map<String, Set<String>> hashtags = new HashMap<String, Set<String>>();
        
        // check if list of tweets is empty
        if (tweets.size() == 0) {
            return hashtags;
        }

        for (Tweet tweet : tweets) {

            String tweetText = tweet.getText();
            
            Set<String> hashtagsByUser = new HashSet<String>();

            // extract individual words from the tweet
            String [] words = tweetText.split(" ");

            // extract the word(s) containing '#'
            for (String word : words) {
                if (word.startsWith("#") && word.length() > 1) {
                    hashtagsByUser.add(word.substring(1));
                }
            }
            
            // add the user and hashtags
            hashtags.put(tweet.getAuthor(), hashtagsByUser);
        }
        
        return hashtags;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
