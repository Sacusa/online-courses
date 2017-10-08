package twitter;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.*;

import twitter.Timespan;
import twitter.Tweet;

public class Extract {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    public enum ExtractVariant {
        GoodCurrentDateAndAlternateCasing, GoodOldDateAndLowercase, BadTimespanOrderAssumption,
        BadTimespanLengthGreaterThanOne, BadTimespanMultiTweetSameStartAndEnd,
        BadTimespanIncorrectEmptyHandling, BadTimespanIOBX, BadTimespanGarbage,
        //BadTimespanPopTwitterList,
        BadGetMentionedUsersTailDelimiting, BadGetMentionedUsersEmailTrap,
        BadGetMentionedUsersFirstMention, BadGetMentionedUsersRepeatedAt,
        BadGetMentionedUsersCaseSensitive //, BadGetMentionUsersModifyOriginalList
    }

    public static ExtractVariant variant = ExtractVariant.GoodCurrentDateAndAlternateCasing;

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        switch (variant) {
        case GoodCurrentDateAndAlternateCasing: return getTimespan_GoodCurrentDate(tweets);
        case GoodOldDateAndLowercase: return getTimespan_GoodOldDate(tweets);
        case BadTimespanOrderAssumption: return getTimespan_BadOrderAssumption(tweets);
        case BadTimespanLengthGreaterThanOne: return getTimespan_BadLengthAssumption(tweets);
        case BadTimespanMultiTweetSameStartAndEnd: return getTimespan_BadRepeatedTimestamps(tweets);
        case BadTimespanIncorrectEmptyHandling: return getTimespan_BadIncorrectEmpty(tweets);
        case BadTimespanIOBX: return getTimespan_BadIOBX(tweets);
        case BadTimespanGarbage: return getTimespan_BadGarbage(tweets);
        //case BadTimespanPopTwitterList: return getTimespan_PopTwitterList(tweets);
        default: return getTimespan_GoodCurrentDate(tweets);
        }
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
        switch (variant) {
        case GoodCurrentDateAndAlternateCasing: return getMentionedUsers_GoodAlternateCase(tweets);
        case GoodOldDateAndLowercase: return getMentionedUsers_GoodLowercase(tweets);
        case BadGetMentionedUsersTailDelimiting: return getMentionedUsers_BadTailDelimiting(tweets);
        case BadGetMentionedUsersEmailTrap: return getMentionedUsers_BadEmailTrap(tweets);
        case BadGetMentionedUsersFirstMention: return getMentionedUsers_BadFirstMention(tweets);
        case BadGetMentionedUsersRepeatedAt: return getMentionedUsers_BadMultipleAt(tweets);
        case BadGetMentionedUsersCaseSensitive: return getMentionedUsers_BadCaseSensitive(tweets);
//        case BadGetMentionUsersModifyOriginalList: return getMentionedUsers_BadModifyOriginalList(tweets);
        default: return getMentionedUsers_GoodAlternateCase(tweets);
        }
    }

    /*************************
     * Good implementations **
     */

    /**
     * Extract.getTimespan()
     */

    private static Timespan getTimespan_GoodCurrentDate(List<Tweet> tweets) {
        // returns Date(), Date() on zero
        // Very interesting case for zero tweets.
        if (tweets.size() == 0) {
            Instant now = Instant.now();
            // Adds by zero to create differnt Instant with the same time
            return new Timespan(now, now.plusMillis(0));
        }
        Instant minDate = null;
        Instant maxDate = null;
        for (Tweet t : tweets) {
            // These two if statements have to be separate for the case of one
            // tweet.
            if (minDate == null) {
                minDate = t.getTimestamp();
                maxDate = t.getTimestamp();
            }
            if (t.getTimestamp().isBefore(minDate)) {
                minDate = t.getTimestamp();
            }
            if (t.getTimestamp().isAfter(maxDate)) {
                maxDate = t.getTimestamp();
            }
        }
        return new Timespan(minDate, maxDate);
    }

    private static Timespan getTimespan_GoodOldDate(List<Tweet> tweets) {
        // returns Date(0), Date(0) on zero
        // Very interesting case for zero tweets.
        if (tweets.size() == 0) {
            Instant epochBeginDateTime = Instant.ofEpochMilli(0);
            return new Timespan(epochBeginDateTime, epochBeginDateTime);
        }
        Instant minDate = null;
        Instant maxDate = null;
        for (Tweet t : tweets) {
            // These two if statements have to be separate for the case of one
            // tweet.
            if (minDate == null) {
                minDate = t.getTimestamp();
                maxDate = t.getTimestamp();
            }
            if (t.getTimestamp().isBefore(minDate)) {
                minDate = t.getTimestamp();
            }
            if (t.getTimestamp().isAfter(maxDate)) {
                maxDate = t.getTimestamp();
            }
        }
        return new Timespan(minDate, maxDate);
    }

    /**
     * getMentionedUsers()
     */

    private static Set<String> getMentionedUsers_GoodLowercase(List<Tweet> tweets) {
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

    private static Set<String> getMentionedUsers_GoodAlternateCase(
            List<Tweet> tweets) {
        // returns alternating case
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            Pattern p = Pattern.compile("(^|[^A-Za-z0-9_-])@[A-Za-z0-9_-]+");
            Matcher m = p.matcher(text);
            while (m.find()) {
                mentions.add(alternateCase(m.group().replaceFirst("^[^A-Za-z0-9_-]+", "")));
            }
        }
        return mentions;
    }

    /************************
     * Bad implementations **
     */

    /**
     * getTimespan()
     */

    private static Timespan getTimespan_BadOrderAssumption(List<Tweet> tweets) {
        // Something akin to assuming that the tweets are ordered.
        Instant now = Instant.now();
        if (tweets.isEmpty()) {
            return new Timespan(now, now);
        }
        return new Timespan(tweets.get(0).getTimestamp(), tweets.get(
                tweets.size() - 1).getTimestamp());
    }

    private static Timespan getTimespan_BadIOBX(List<Tweet> tweets) {
        // Explodes with IOBX when tweets are empty.
        if (tweets.get(0) == null) {
            Instant now = Instant.now();
            return new Timespan(now, now);
        }
        Instant minDate = Instant.now();
        Instant maxDate = Instant.ofEpochMilli(0);
        for (Tweet t : tweets) {
            if (t.getTimestamp().isBefore(minDate)) {
                minDate = t.getTimestamp();
            }
            if (t.getTimestamp().isAfter(maxDate)) {
                maxDate = t.getTimestamp();
            }
        }
        return new Timespan(minDate, maxDate);
    }

    private static Timespan getTimespan_BadGarbage(List<Tweet> tweets) {
        // Always returns the same date.
        Instant d1 = Instant.parse("1992-10-23T00:00:00Z");
        return new Timespan(d1, d1);
    }

    private static Timespan getTimespan_PopTwitterList(List<Tweet> tweets) {
        // works the same way as getTimespan_GoodOldDate, but by popping the list
        // returns Date(0), Date(0) on zero
        // Very interesting case for zero tweets.
        if (tweets.size() == 0) {
            Instant epochBeginDateTime = Instant.ofEpochMilli(0);
            return new Timespan(epochBeginDateTime, epochBeginDateTime);
        }
        Instant minDate = null;
        Instant maxDate = null;
        while (!tweets.isEmpty()) {
            Tweet t = tweets.remove(0);
            // These two if statements have to be separate for the case of one
            // tweet.
            if (minDate == null) {
                minDate = t.getTimestamp();
                maxDate = t.getTimestamp();
            }
            if (t.getTimestamp().isBefore(minDate)) {
                minDate = t.getTimestamp();
            }
            if (t.getTimestamp().isAfter(maxDate)) {
                maxDate = t.getTimestamp();
            }
        }
        return new Timespan(minDate, maxDate);
    }

    private static Timespan getTimespan_BadLengthAssumption(List<Tweet> tweets) {
        // Assumes tweets.size() != 1.
        
        if (tweets.isEmpty()) {
            Instant now = Instant.now();
            return new Timespan(now, now);
        }

        Instant start = Instant.now();
        Instant end = Instant.ofEpochMilli(0);
        
        for (Tweet t : tweets) {
            if (t.getTimestamp().isBefore(start)) {
                start = t.getTimestamp();
            } else {
                end = t.getTimestamp();
            }
        }
        if (end.isBefore(start)) {
            for (Tweet t : tweets) {
                if (t.getTimestamp().equals(start)) {
                    continue;
                } else if (t.getTimestamp().isAfter(end)) {
                    end = t.getTimestamp();
                } else {
                    continue;
                }
            }
        }
        return new Timespan(start, end);
    }

    private static Timespan getTimespan_BadRepeatedTimestamps(List<Tweet> tweets) {
        // misses case when two tweets have same date
        if (tweets.isEmpty()) {
            Instant now = Instant.now();
            return new Timespan(now, now);
        }

        if (tweets.size() == 1) {
            return new Timespan(tweets.get(0).getTimestamp(), tweets.get(0)
                    .getTimestamp());
        }

        Instant start = Instant.now();
        Instant end = Instant.ofEpochMilli(0);
        for (Tweet t : tweets) {
            if (t.getTimestamp().isBefore(start)) {
                start = t.getTimestamp();
            } else if (t.getTimestamp().equals(start)) {
                continue;
            } else {
                end = t.getTimestamp();
            }
        }
        if (end.isBefore(start)) {
            for (Tweet t : tweets) {
                if (t.getTimestamp().equals(start)) {
                    continue;
                } else if (t.getTimestamp().isAfter(end)) {
                    end = t.getTimestamp();
                } else {
                    continue;
                }
            }
        }
        return new Timespan(start, end);
    }

    private static Timespan getTimespan_BadIncorrectEmpty(List<Tweet> tweets) {
        // Returns incorrect timespan for empty list.
        if (tweets.size() == 0) {
            Instant epochBeginDateTime = Instant.ofEpochMilli(0);
            return new Timespan(epochBeginDateTime , Instant.now());
        }
        
        Instant minDate = Instant.now();
        Instant maxDate = Instant.ofEpochMilli(0);
        for (Tweet t : tweets) {
            if (t.getTimestamp().isBefore(minDate)) {
                minDate = t.getTimestamp();
            }
            if (t.getTimestamp().isAfter(maxDate)) {
                maxDate = t.getTimestamp();
            }
        }
        return new Timespan(minDate, maxDate);
    }

    /**
     * getMentionedUsers()
     */
    
    private static Set<String> getMentionedUsers_BadFirstMention(List<Tweet> tweets) {
        // Only returns the very first mentioned user.
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            for (String mention : text.split(" ")) {
                if (mention.contains("@")) {
                    // Trim all leading punctuation.
                    String updatedMention = mention.replaceFirst(
                            "^[^a-zA-Z0-9_-]+", "");
                    /*
                     * If there's nothing left, it wasn't valid. Similarly, if
                     * the user-mention still contains an @ sign, it means there
                     * was something incorrect in there.
                     */
                    if (!updatedMention.equals("")
                            && !updatedMention.contains("@")) {
                        /*
                         * We split on non-username characters to ensure that we
                         * aren't grabbing things like an 's. We don't strip
                         * this punctuation because doing so would cause us to
                         * get results like @rbmllr's -> rbmllrs.
                         */
                        String[] mentionSplit = updatedMention
                                .split("[^a-zA-Z0-9_-]+");
                        return new HashSet<String>(
                                Arrays.asList(mentionSplit[0].toLowerCase()));
                    }
                }
            }
        }

        return mentions;
    }
    
    private static Set<String> getMentionedUsers_BadCaseSensitive(List<Tweet> tweets) {
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

    private static Set<String> getMentionedUsers_BadTailDelimiting(List<Tweet> tweets) {
        /*
         * Gets everything but tricky punctuation: @rbmllr's -> rbmllr's
         */
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            for (String mention : text.split(" ")) {
                if (mention.contains("@")) {
                    // Trim all leading punctuation.
                    String updatedMention = mention.replaceFirst(
                            "^[^a-zA-Z0-9_-]+", "");
                    /*
                     * If there's nothing left, it wasn't valid. Similarly, if
                     * the user-mention still contains an @ sign, it means there
                     * was something incorrect in there.
                     */
                    if (!updatedMention.equals("")
                            && !updatedMention.contains("@")) {
                        mentions.add(updatedMention.toLowerCase());
                    }
                }
            }
        }

        return mentions;
    }

    private static Set<String> getMentionedUsers_BadEmailTrap(List<Tweet> tweets) {
        // grabs emails (wvyar@mit.edu -> mit)
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            for (String mention : text.split(" ")) {
                if (mention.contains("@")) {
                    // Trim everything up to the first @.
                    String updatedMention = mention.replaceFirst("^[^@]+", "");
                    // Trim all preceding non-username characters characters.
                    String updatedMention2 = updatedMention.replaceFirst(
                            "^[^a-zA-Z0-9_-]+", "");
                    /*
                     * If there's nothing left, it wasn't valid. Similarly, if
                     * the user-mention still contains an @ sign, it means there
                     * was something incorrect in there.
                     */
                    if (!updatedMention.equals("")) {
                        /*
                         * We split on non-username characters to ensure that we
                         * aren't grabbing things like an 's. We don't strip
                         * this punctuation because doing so would cause us to
                         * get results like @rbmllr's -> rbmllrs.
                         */
                        String[] mentionSplit = updatedMention2
                                .split("[^a-zA-Z0-9_-]+");
                        mentions.add(mentionSplit[0].toLowerCase());
                    }
                }
            }
        }

        return mentions;
    }

    private static Set<String> getMentionedUsers_BadMultipleAt(List<Tweet> tweets) {
        // breaks on multiple @ symbols
        Set<String> mentions = new HashSet<String>();
        for (Tweet t : tweets) {
            String text = t.getText();
            for (String mention : text.split(" ")) {
                if (mention.contains("@")) {
                    int indexOf = mention.indexOf("@");

                    if (indexOf != 0
                            && mention.substring(indexOf - 1, indexOf).matches(
                                    "[a-zA-Z0-9_-]")) {
                        // invalid because of preceding characters
                        continue;
                    }

                    String tmpString = mention.replaceFirst("^[^a-zA-Z0-9_-@]+", "").substring(1);
                    
                    if (tmpString.equals("")) {
                        continue;
                    }
                    
                    String[] mentionTokens = tmpString.split("@");

                    for (String s : mentionTokens) {
                        // strip any extraneous punctuation!
                        String[] mentionSplit = s.split("[^a-zA-Z0-9_-]+");
                        if (mentionSplit.length != 0) {
                            mentions.add(mentionSplit[0].toLowerCase());
                        }
                    }
                }
            }
        }
        
        return mentions;
    }
//
//    private static Set<String> getMentionedUsers_BadModifyOriginalList(List<Tweet> tweets) {
//        // converts every tweet's text and author to lowercase, modifying the original list
//        for (int i = 0; i < tweets.size(); i ++) {
//            Tweet o = tweets.get(i);
//            Tweet n = new Tweet(o.getId(), o.getAuthor().toLowerCase(),
//                    o.getText().toLowerCase(), o.getTimestamp());
//            tweets.set(i, n);
//        }
//        Set<String> mentions = new HashSet<String>();
//        for (Tweet t : tweets) {
//            String text = t.getText();
//            Pattern p = Pattern.compile("(^|[^a-z0-9_-])@[a-z0-9_-]+");
//            Matcher m = p.matcher(text);
//            while (m.find()) {
//                mentions.add(m.group().replaceFirst("^[^a-z0-9_-]+", ""));
//            }
//        }
//        return mentions;
//    }

    /*********************
     * Helper Functions **
     */

    private static String alternateCase(String s) {
        String retString = "";
        // always start lowercase
        boolean capital = false;
        for (int i = 0; i < s.length(); i++) {
            if (capital) {
                retString += s.substring(i, i + 1).toUpperCase();
                capital = false;
            } else {
                retString += s.substring(i, i + 1).toLowerCase();
                capital = true;
            }
        }
        return retString;
    }

}
