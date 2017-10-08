package autograder;

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
 * just check for the right message
 * 
 * @author njoliat
 */
public class BoomTest {

    @Test(timeout=10000)
    public void testBoom() throws IOException, InterruptedException {
        Socket sock = TestUtil.connect(TestUtil.startServer(false, "board_file_2"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            
            //burn welcome message
            try {
                in.readLine();
            } catch (SocketTimeoutException ignored){}
            
            out.println("dig 1 1");
            assertEquals("BOOM!", in.readLine());
        } catch (SocketTimeoutException e) {
            fail("readLine timed out");
        } finally {
            sock.close();
        }
    }
}
