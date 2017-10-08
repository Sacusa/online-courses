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
 * check to make sure the server disconnects us after we get a boom.
 * implemented in a prettty hacky way
 * 
 * @author njoliat
 */
public class BoomDisconnectTest {

    @Test(timeout=10000)
    public void testBoomDisconnect() throws IOException, InterruptedException {
        Socket sock = TestUtil.connect(TestUtil.startServer(false, "board_file_2"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            
            //burn welcome message
            try {
                in.readLine();
            } catch (SocketTimeoutException ignored){}
            
            assertTrue("never connected to server", sock.isConnected());
            
            out.println("dig 1 1");
            
            // burn through any server messages
            // (i don't care about checking them for this test)
            for(int i=0; i<20; i++) {
                in.readLine();
            }
            
            assertTrue("if we're disconnected readLine should give a null",in.readLine() == null);
        } catch (SocketTimeoutException e) {
            fail("readLine timed out");
        } finally {
            sock.close();
        }
    }
}
