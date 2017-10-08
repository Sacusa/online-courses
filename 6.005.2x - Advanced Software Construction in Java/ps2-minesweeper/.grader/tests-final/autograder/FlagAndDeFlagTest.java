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

public class FlagAndDeFlagTest {

    @Test(timeout=10000)
    public void testFlagThenDeflag() throws IOException, InterruptedException {
        final String expected[] = new String[] {
                "- - - - - -",
                "- - - - - -",
                "- - F - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -"
        };

        Socket sock = TestUtil.connect(TestUtil.startServer(false, "board_file_2"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            String moo;
            
            //throw away first line; look for BOARD msg.
            try{
                moo = in.readLine();
            }catch(SocketTimeoutException ignored){}
            
            out.println("flag 2 2");
            for(int i=0; i<expected.length; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[i]), nosp(moo));
            }
            
            out.println("deflag 2 2");
            for(int i=0; i<expected.length; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[0]), nosp(moo));
            }
        } catch (SocketTimeoutException e) {
            //want to get this on third readLine.
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
