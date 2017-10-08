package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.*;

import twitter.Tweet;

public class SocialNetwork {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    public enum SocialNetworkVariant {
        GoodGraphEmptyNodes, GoodGraphNoEmptyNodes, GoodGraphAlternatingCase, GoodGraphReflexive,
        GoodInfluencersHashcode, GoodInfluencersReverseHashcode, BadGraphComplete, BadGraphSelfLoop,
        BadGraphCaseSensitive, BadGraphBadUsernames, BadGraphWrongDirection, BadInfluencersCaseSensitive,
        BadInfluencersPruneEmpty, BadInfluencersFirstOnly //, BadInfluencersSortByRemoving
    }

    public static SocialNetworkVariant variant = SocialNetworkVariant.GoodGraphEmptyNodes;
    
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
        switch (variant) {
        case GoodGraphEmptyNodes: return guessFollowsGraph_GoodEmptyNodes(tweets);
        case GoodGraphNoEmptyNodes: return guessFollowsGraph_GoodNoEmptyNodes(tweets);
        case GoodGraphAlternatingCase: return guessFollowsGraph_GoodAlternatingCase(tweets);
        case GoodGraphReflexive: return guessFollowsGraph_GoodReflexive(tweets);
        case BadGraphComplete: return guessFollowsGraph_BadComplete(tweets);
        case BadGraphSelfLoop: return guessFollowsGraph_BadSelfies(tweets);
        case BadGraphCaseSensitive: return guessFollowsGraph_BadCaseSensitive(tweets);
        case BadGraphBadUsernames: return guessFollowsGraph_BadGarbageUsernames(tweets);
        case BadGraphWrongDirection: return guessFollowsGraph_BadWrongDirection(tweets);
        default: return guessFollowsGraph_GoodEmptyNodes(tweets);
        }
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
        switch (variant) {
        case GoodInfluencersHashcode: return influencers_GoodHashcode(followsGraph);
        case GoodInfluencersReverseHashcode: return influencers_GoodReverseHashcode(followsGraph);
        case BadInfluencersCaseSensitive: return influencers_BadCaseSensitive(followsGraph);
        case BadInfluencersPruneEmpty: return influencers_BadPruneEmpty(followsGraph);
        case BadInfluencersFirstOnly: return influencers_BadFirstOnly(followsGraph);
//        case BadInfluencersSortByRemoving: return influencers_BadSortByRemoving(followsGraph);
        default: return influencers_GoodHashcode(followsGraph);
        }
    }
    
    /*************************
     * Good implementations **
     */
    
    /**
     * guessFollowsGraph()
     */
    
    private static Map<String, Set<String>> guessFollowsGraph_GoodEmptyNodes(List<Tweet> tweets) {
        // Contains nodes that aren't following anyone.
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor().toLowerCase());
            if (network.get(t.getAuthor().toLowerCase()) == null) {
                network.put(t.getAuthor().toLowerCase(), userFriends);
            } else {
                network.get(t.getAuthor().toLowerCase()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_GoodNoEmptyNodes(List<Tweet> tweets) {
        // Does not contain nodes not following anyone.
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor().toLowerCase());
            if (userFriends.isEmpty()) {
                continue;
            }
            if (network.get(t.getAuthor().toLowerCase()) == null) {
                network.put(t.getAuthor().toLowerCase(), userFriends);
            } else {
                network.get(t.getAuthor().toLowerCase()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_GoodAlternatingCase(List<Tweet> tweets) {
        // Authors are capitalized, followed users are lowercase.
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor().toLowerCase());
            if (network.get(t.getAuthor().toUpperCase()) == null) {
                network.put(t.getAuthor().toUpperCase(), userFriends);
            } else {
                network.get(t.getAuthor().toUpperCase()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_GoodReflexive(List<Tweet> tweets) {
        // Reflexive friends relationships.
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor().toLowerCase());
            if (network.get(t.getAuthor().toLowerCase()) == null) {
                network.put(t.getAuthor().toLowerCase(), userFriends);
            } else {
                network.get(t.getAuthor().toLowerCase()).addAll(userFriends);
            }
            for (String user : userFriends) {
                if (network.get(user) == null) {
                    network.put(user, new HashSet<String>(Arrays.asList(t.getAuthor().toLowerCase())));
                } else {
                    network.get(user).add(t.getAuthor().toLowerCase());
                }
            }
        }
        
        return network;
    }
    
    /**
     * influencers()
     */
    
    private static List<String> influencers_GoodHashcode(Map<String, Set<String>> followsGraph) {
        // this impl sorts equally-ranked users by hashcode
        /*
         * We'll store them as an array to start. This allows us to iterate
         * through the graph, comparing each user to every other user to
         * determine a baseline "influenceRank," which is the highest ranking
         * they could achieve in the influenceRanking. A larger set in the
         * followsGraph means a higher ranking.
         */
        // Craft a set containing all possible users.
        Set<String> allUsers = new HashSet<String>(
                setToLowerCase(followsGraph.keySet()));
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(setToLowerCase(followsGraph.get(user)));
        }

        // String[] influenceRanking = new String[allUsers.size()];

        /*
         * We construct a mapping of number of followers to a set of users who
         * possess that many followers.
         */
        Map<Integer, Set<String>> influenceRanking = new HashMap<Integer, Set<String>>();
        int maxInfluence = 0;

        List<String> influencers = new ArrayList<String>();

        for (String user : allUsers) {
            // Start off at 0 influence.
            int influence = 0;

            for (String author : followsGraph.keySet()) {
                if (setToLowerCase(followsGraph.get(author)).contains(user)) {
                    // If you're mentioned, bump yourself further up.
                    influence++;
                }
            }

            if (influence > maxInfluence) {
                maxInfluence = influence;
            }

            if (influenceRanking.get(influence) == null) {
                influenceRanking.put(influence, new HashSet<String>());
            }

            influenceRanking.get(influence).add(user);
        }

        for (int i = maxInfluence; i >= 0; i--) {
            if (influenceRanking.get(i) != null) {
                if (influenceRanking.get(i).size() > 1) {
                    influencers.addAll(setToHashcodeSort(influenceRanking.get(i)));
                } else {
                    influencers.addAll(influenceRanking.get(i));
                }
            }
        }

        return influencers;
    }
    
    private static List<String> influencers_GoodReverseHashcode(Map<String, Set<String>> followsGraph) {
        // this impl sorts equally-ranked users by reverse hashcode
        /*
         * We'll store them as an array to start. This allows us to iterate
         * through the graph, comparing each user to every other user to
         * determine a baseline "influenceRank," which is the highest ranking
         * they could achieve in the influenceRanking. A larger set in the
         * followsGraph means a higher ranking.
         */
        // Craft a set containing all possible users.
        Set<String> allUsers = new HashSet<String>(
                setToLowerCase(followsGraph.keySet()));
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(setToLowerCase(followsGraph.get(user)));
        }

        // String[] influenceRanking = new String[allUsers.size()];

        /*
         * We construct a mapping of number of followers to a set of users who
         * possess that many followers.
         */
        Map<Integer, Set<String>> influenceRanking = new HashMap<Integer, Set<String>>();
        int maxInfluence = 0;

        List<String> influencers = new ArrayList<String>();

        for (String user : allUsers) {
            // Start off at 0 influence.
            int influence = 0;

            for (String author : followsGraph.keySet()) {
                if (setToLowerCase(followsGraph.get(author)).contains(user)) {
                    // If you're mentioned, bump yourself further up.
                    influence++;
                }
            }

            if (influence > maxInfluence) {
                maxInfluence = influence;
            }

            if (influenceRanking.get(influence) == null) {
                influenceRanking.put(influence, new HashSet<String>());
            }

            influenceRanking.get(influence).add(user);
        }

        for (int i = maxInfluence; i >= 0; i--) {
            if (influenceRanking.get(i) != null) {
                if (influenceRanking.get(i).size() > 1) {
                    influencers.addAll(setToReverseHashcodeSort(influenceRanking.get(i)));
                } else {
                    influencers.addAll(influenceRanking.get(i));
                }
            }
        }

        return influencers;
    }
    
    
    /************************
     * Bad implementations **
     */
    
    /**
     * guessFollowsGraph()
     */
    
    private static Map<String, Set<String>> guessFollowsGraph_BadComplete(List<Tweet> tweets) {
        // just connects everyone together
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        for (Tweet t: tweets) {
            network.put(t.getAuthor(), null);
        }
        
        for (String user: network.keySet()) {
            network.put(user, network.keySet());
        }
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_BadSelfies(List<Tweet> tweets) {
        // self follows allowed
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        // Simple implementation - uses direct mentions.
        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            if (network.get(t.getAuthor()) == null) {
                network.put(t.getAuthor(), userFriends);
            } else {
                network.get(t.getAuthor()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_BadCaseSensitive(List<Tweet> tweets) {
        // issues with case sensitivity
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        // Simple implementation - uses direct mentions.
        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_CaseSensitive(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor());
            if (network.get(t.getAuthor()) == null) {
                network.put(t.getAuthor(), userFriends);
            } else {
                network.get(t.getAuthor()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_BadGarbageUsernames(List<Tweet> tweets) {
        // map to usernames that don't exist/aren't mentioned
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        // Simple implementation - uses direct mentions.
        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(Arrays.asList(t.getText().split("\\s+")));
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            // Can't follow yourself.
            userFriends.remove(t.getAuthor());
            if (network.get(t.getAuthor()) == null) {
                network.put(t.getAuthor(), userFriends);
            } else {
                network.get(t.getAuthor()).addAll(userFriends);
            }
        }
        
        return network;
    }
    
    private static Map<String, Set<String>> guessFollowsGraph_BadWrongDirection(List<Tweet> tweets) {
        Map<String, Set<String>> network = new HashMap<String, Set<String>>();
        Set<String> userFriends;

        // Simple implementation - uses direct mentions.
        for (Tweet t : tweets) {
            userFriends = new HashSet<String>();
            userFriends.addAll(getMentionedUsers_Good(Arrays.asList(t)));
            for (String u : userFriends) {
                if (network.get(u.toLowerCase()) == null) {
                    network.put(u.toLowerCase(), new HashSet<String>(Arrays.asList(t.getAuthor().toLowerCase())));
                } else {
                    network.get(u.toLowerCase()).add(t.getAuthor().toLowerCase());
                }
            }

        }
        
        return network;
    }
    
    /**
     * influencers()
     */
    
    private static List<String> influencers_BadPruneEmpty(Map<String, Set<String>> followsGraph) {
        // Doesn't include anyone with influence of 0.
        Set<String> allUsers = new HashSet<String>(
                setToLowerCase(followsGraph.keySet()));
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(setToLowerCase(followsGraph.get(user)));
        }

        Map<Integer, Set<String>> influenceRanking = new HashMap<Integer, Set<String>>();
        int maxInfluence = 0;
        List<String> influencers = new ArrayList<String>();
        for (String user : allUsers) {
            int influence = 0;
            for (String author : followsGraph.keySet()) {
                if (setToLowerCase(followsGraph.get(author)).contains(user)) {
                    influence++;
                }
            }
            if (influence > maxInfluence) {
                maxInfluence = influence;
            }
            if (influenceRanking.get(influence) == null) {
                influenceRanking.put(influence, new HashSet<String>());
            }
            if (influence != 0) {
                influenceRanking.get(influence).add(user);
            }
        }
        for (int i = maxInfluence; i >= 0; i--) {
            if (influenceRanking.get(i) != null) {
                influencers.addAll(influenceRanking.get(i));
            }
        }
        return influencers;
    }
    
    private static List<String> influencers_BadCaseSensitive(Map<String, Set<String>> followsGraph) {
        // Isn't case-insensitive.
        Set<String> allUsers = new HashSet<String>(followsGraph.keySet());
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(followsGraph.get(user));
        }

        Map<Integer, Set<String>> influenceRanking = new HashMap<Integer, Set<String>>();
        int maxInfluence = 0;
        List<String> influencers = new ArrayList<String>();
        for (String user : allUsers) {
            int influence = 0;
            for (String author : followsGraph.keySet()) {
                if (followsGraph.get(author).contains(user)) {
                    influence++;
                }
            }
            if (influence > maxInfluence) {
                maxInfluence = influence;
            }
            if (influenceRanking.get(influence) == null) {
                influenceRanking.put(influence, new HashSet<String>());
            }
            influenceRanking.get(influence).add(user);
        }
        for (int i = maxInfluence; i >= 0; i--) {
            if (influenceRanking.get(i) != null) {
                influencers.addAll(influenceRanking.get(i));
            }
        }
        return influencers;
    }
    
    private static List<String> influencers_BadFirstOnly(Map<String, Set<String>> followsGraph) {
        // Only outputs one person for each rank.
        Set<String> allUsers = new HashSet<String>(
                setToLowerCase(followsGraph.keySet()));
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(setToLowerCase(followsGraph.get(user)));
        }

        Map<Integer, String> influenceRanking = new HashMap<Integer, String>();
        int maxInfluence = 0;
        List<String> influencers = new ArrayList<String>();
        for (String user : allUsers) {
            int influence = 0;
            for (String author : followsGraph.keySet()) {
                if (setToLowerCase(followsGraph.get(author)).contains(user)) {
                    influence++;
                }
            }
            if (influence > maxInfluence) {
                maxInfluence = influence;
            }
            influenceRanking.put(influence, user);
        }
        for (int i = maxInfluence; i >= 0; i--) {
            if (influenceRanking.get(i) != null) {
                influencers.add(influenceRanking.get(i));
            }
        }
        return influencers;
    }
    
    private static List<String> influencers_BadSortByRemoving(Map<String, Set<String>> followsGraph) {
        Set<String> allUsers = new HashSet<String>(
                setToLowerCase(followsGraph.keySet()));
        for (String user : followsGraph.keySet()) {
            allUsers.addAll(setToLowerCase(followsGraph.get(user)));
        }
        // this list will be in the opposite direction until it is reversed before return
        List<String> influencers = new ArrayList<String>();
        while (!allUsers.isEmpty()) {
            for (Iterator<String> itUser = allUsers.iterator(); itUser.hasNext(); ) {
                String user = itUser.next();
                boolean presence = false;
                for (Set<String> followedSet : followsGraph.values()) {
                    for (String followedUser : followedSet) {
                        if (user.equalsIgnoreCase(followedUser)) {
                            followedSet.remove(followedUser);
                            presence = true;
                            break;
                        }
                    }
                    if (presence) {
                        break;
                    }
                }
                if (!presence) {
                    influencers.add(user);
                    itUser.remove();
                }
            }
        }
        Collections.reverse(influencers);
        return influencers;
    }

    /*********************
     * Helper functions **
     */
    
    private static Set<String> getMentionedUsers_Good(List<Tweet> tweets) {
        // ripped from a good version of extract
        // returns all lowercase
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            Pattern p = Pattern.compile("(^|[^A-Za-z0-9_-])@[A-Za-z0-9_-]+");
            Matcher m = p.matcher(text);
            while (m.find()) {
                mentions.add(m.group().replaceFirst("^[^A-Za-z0-9_-]+", "").toLowerCase());
            }
        }
        return mentions;
    }
    
    private static Set<String> getMentionedUsers_CaseSensitive(List<Tweet> tweets) {
        // not case-insensitive
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            Pattern p = Pattern.compile("(^|[^A-Za-z0-9_-])@[A-Za-z0-9_-]+");
            Matcher m = p.matcher(text);
            while (m.find()) {
                mentions.add(m.group().replaceFirst("^[^A-Za-z0-9_-]+", ""));
            }
        }
        return mentions;
    }
    
    private static Set<String> setToLowerCase(Set<String> strings) {
        Set<String> lowerSet = new HashSet<String>();
        for (String s : strings) {
            lowerSet.add(s.toLowerCase());
        }
        return lowerSet;
    }
    
    private static Set<String> setToUpperCase(Set<String> strings) {
        Set<String> upperSet = new HashSet<String>();
        for (String s : strings) {
            upperSet.add(s.toUpperCase());
        }
        return upperSet;
    }
    
    private static List<String> setToReverseHashcodeSort(Set<String> strings) {
        List<String> sorted = new ArrayList<String>(strings);
        Collections.sort(sorted, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.hashCode() - o2.hashCode();
            }
        });
        return sorted;
    }
    
    private static List<String> setToHashcodeSort(Set<String> strings) {
        List<String> sorted = new ArrayList<String>(strings);
        Collections.sort(sorted, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o2.hashCode() - o1.hashCode();
            }
        });
        return sorted;
    }

}
