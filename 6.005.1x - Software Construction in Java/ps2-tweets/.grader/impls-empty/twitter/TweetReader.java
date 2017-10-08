package twitter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Read tweets from files or from a web server. Uses a simplified representation
 * for tweets (with fewer fields than the Twitter API).
 * 
 * DO NOT CHANGE THIS CLASS.
 */
public class TweetReader {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
    
    /**
     * Get a list of tweets from a web server.
     * 
     * @param url URL of server to retrieve tweets from
     * @return a list of tweets retrieved from the server.
     * @throws IOException if the url is invalid, the server is unreachable,
     *                     or some other network-related error occurs.
     */
    public static List<Tweet> readTweetsFromWeb(URL url) throws IOException {
        return null;
    }
}
