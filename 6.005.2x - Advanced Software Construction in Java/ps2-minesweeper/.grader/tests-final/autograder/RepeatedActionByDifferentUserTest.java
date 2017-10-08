package autograder;

import static autograder.TestUtil.nosp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.Test;

public class RepeatedActionByDifferentUserTest {

    @Test(timeout=10000)
    public void testRepeatDigByDifferentUsers() throws IOException, InterruptedException {
        final String expected[] = new String[] {
                "3 - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -"
        };

        final ThreadWithObituary serverThread = TestUtil.startServer(false, "board_file_2");

        Socket sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            out.println("dig 0 0");
            out.println("dig 0 0");
            String moo;

            //throw away first line + 2 boards msgs.
            try {
                for(int i=0; i<(2*expected.length + 1); i++) {
                    moo = in.readLine();
                }
            } catch (SocketTimeoutException ignored) { }
            
            out.println("dig 0 0");
            for(int i=0; i<expected.length; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[i]), nosp(moo));
            }
        } catch (SocketTimeoutException e) {
            //want to get this on third readLine.
            fail("server timeout");
        } finally {
            sock.close();
        }

        sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            String moo;
            
            //throw away first line; look for BOARD msg.
            try {
                moo = in.readLine();
            } catch (SocketTimeoutException ignored) { }

            out.println("dig 0 0");
            for(int i=0; i<expected.length; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[i]), nosp(moo));
            }
        } catch (SocketTimeoutException e) {
            //want to get this on third readLine.
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
