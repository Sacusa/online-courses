package twitter;

import java.util.stream.Collectors;
import java.util.*;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        final Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        // START OF BASIC IMPLEMENTATION
        
        for (Tweet tweet : tweets) {
            Set<String> followedUsers = Extract.getMentionedUsers(Arrays.asList(tweet));
            Set<String> followedUsersLowerCase = new HashSet<String>();
            String author = tweet.getAuthor().toLowerCase();
            
            // convert all entries to lower case
            for (String user : followedUsers) {
                String userLowerCase = user.toLowerCase();
                
                // skip the author
                if (userLowerCase.equals(author)) {
                    continue;
                }
                
                followedUsersLowerCase.add(userLowerCase);
            }
            
            map.put(author, followedUsersLowerCase);
        }
        
        // END OF BASIC IMPLEMENTATION

        // START OF 'COMMON HASHTAGS' TECHNIQUE
        
        Map<String, Set<String>> hashtagsMap = Extract.getHashtags(tweets);
        
        for (String username : hashtagsMap.keySet()) {
            for (String compareUsername : hashtagsMap.keySet()) {
                
                // get next username if the two usernames are the same
                if (username.equals(compareUsername)) {
                    continue;
                }
                
                // get the two sets of hashtags
                Set<String> hashtags = hashtagsMap.get(username);
                Set<String> compareHashtags = hashtagsMap.get(compareUsername);
                
                // the users are related if they have atleast one hashtag in common
                for (String hashtag : hashtags) {
                    if (compareHashtags.contains(hashtag)) {
                        
                        // add compareUsername to username's followers
                        Set<String> existingFollowers = map.get(username);
                        existingFollowers.add(compareUsername);
                        map.put(username.toLowerCase(), existingFollowers);
                        
                        // add username to compareUsername's followers
                        existingFollowers = map.get(compareUsername);
                        existingFollowers.add(username);
                        map.put(compareUsername.toLowerCase(), existingFollowers);                        
                    }
                }
            }
        }
        
        // END OF 'COMMON HASHTAGS' TECHNIQUE
        
        return map;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> numberOfFollowers = new TreeMap<String, Integer>();
        
        for (String username : followsGraph.keySet()) {
            
            // add entry if not present
            if (!numberOfFollowers.containsKey(username)) {
                numberOfFollowers.put(username.toLowerCase(), 0);
            }
            
            for (String influencedUser : followsGraph.get(username)) {
                
                Integer existingNumberOfFollowers = numberOfFollowers.get(influencedUser.toLowerCase());
                
                // define a new entry for influencedUser, if not defined before
                if (existingNumberOfFollowers == null) {
                    numberOfFollowers.put(influencedUser.toLowerCase(), 1);
                }
                
                // increase the followers of influencedUser
                else {
                    numberOfFollowers.put(influencedUser.toLowerCase(), ++existingNumberOfFollowers);
                }
            }
        }
        
        // sort the map numberOfFollowers
        numberOfFollowers = sortByValue(numberOfFollowers);
        
        // construct the list of keys i.e. users
        List<String> mostInfluential = new ArrayList<String>();
        mostInfluential.addAll(numberOfFollowers.keySet());
        
        return mostInfluential;
    }
    
    /**
     * Sort the keys in the map by their values in descending order,
     * given that the values are comparable.
     * 
     * Taken from: http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java#2581754
     * 
     * @param map
     * 
     * @return a sorted Map, sorted by values in their descending order.
     *         For two keys with same values, natural ordering of keys is done.
     */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, 
                        Map.Entry::getValue, 
                        (e1, e2) -> e1, 
                        LinkedHashMap::new
                        ));
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
