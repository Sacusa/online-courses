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

/**
 * @author njoliat
 */
public class ExplodeTest {
    
    @Test(timeout=10000)
    public void testExplode() throws IOException, InterruptedException {
        final String expected[] = new String[] {
                "- - - - - -",
                "- 2 - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -"
        };

        Socket sock = TestUtil.connect(TestUtil.startServer(true, "board_file_4"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            String moo;
            
            //burn the hello msg and the boom msg
            try {
                moo = in.readLine();
            } catch(SocketTimeoutException ignored){}
            
            out.println("dig 1 1");
            try {
                moo = in.readLine();
            } catch (SocketTimeoutException ignored) {}
            
            out.println("look");
            for(int i=0; i<6; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[i]), nosp(moo));
            }
        } catch (SocketTimeoutException e) {
            fail("readLine timed out");
        } finally {
            sock.close();
        }
    }
}
