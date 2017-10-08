package twitter.staff;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;

public class TestHelpers {

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    private static String summarizeResult(Result res) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(result, true);
        out.printf("%d test%s ran with %d failure%s in %d ms.\n",
                   res.getRunCount(), res.getRunCount() == 1 ? "" : "s",
                   res.getFailureCount(), res.getFailureCount() == 1 ? "" : "s",
                   res.getRunTime());
        for (Failure failure : res.getFailures()) {
            out.println(failure.getException().getClass().getName() + ": " + failure.getMessage());

            int elided = 0;
            for (StackTraceElement elem : failure.getException().getStackTrace()) {
                String cls = elem.getClassName();
                if (cls.startsWith("twitter.") && !cls.startsWith("twitter.staff.")) {
                    if (elided != 0) {
                        out.println("    ...");
                        elided = 0;
                    }
                    out.println("    at " + elem);
                } else {
                    elided++;
                }
            }
            if (elided != 0) {
                out.println("    ...");
            }
        }
        return result.toString().trim();
    }

    public static void assertSuccess(Result res) {
        if ( ! res.wasSuccessful()) {
            fail("Some of your tests failed when run against our correct implementation: "
                 + summarizeResult(res) + "\n----");
        } else {
            // System.out.println(summarizeResult(res));
        }
    }

    public static void assertFailure(Result res) {
        if (res.wasSuccessful()) {
            fail("None of your tests failed when run against our broken implementation: "
                 + summarizeResult(res));
        } else {
            // System.out.println(summarizeResult(res));
        }
    }
}
