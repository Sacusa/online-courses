package autograder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.Test;

/**
 * check to make sure the server disconnects us after we send it "bye".
 * implemented in a prettty hacky way
 * 
 * @author njoliat
 */
public class ByeTest {

    @Test(timeout=10000)
    public void testBye() throws IOException, InterruptedException {
        Socket sock = TestUtil.connect(TestUtil.startServer(false));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            
            assertTrue("never connected to server", sock.isConnected());
            
            out.println("bye");
            
            // burn through any server messages
            // (i don't care about spurious ones for this test)
            for(int i=0; i<20; i++) {
                in.readLine();
            }
            
            // it's probable that the server won't be sending a bunch of 
            // nulls to us, so if we get this it's probably because the
            // server booted us, which is what we want.
            assertTrue("if we're disconnected readLine should give a null",in.readLine() == null);
        } catch (SocketTimeoutException e) {
            fail("readLine timed out");
        } finally {
            sock.close();
        }
    }
}
