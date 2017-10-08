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

public class BlankBoardTest {

    @Test(timeout=10000)
    public void testBlankBoard() throws IOException, InterruptedException {
        final String expected = "- - - - - - - - - -";
        
        Socket sock = TestUtil.connect(TestUtil.startServer(false));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            //throw away first line; look for BOARD msg.
            try {
                in.readLine();
            } catch (SocketTimeoutException ignored){}
            
            out.println("look");
            for(int i=0; i<10; i++) {
                assertEquals(expected, in.readLine());
            }
        } catch (SocketTimeoutException e) {
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
