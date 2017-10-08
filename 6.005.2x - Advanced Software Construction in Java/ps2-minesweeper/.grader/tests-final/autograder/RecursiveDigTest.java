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

public class RecursiveDigTest {

    @Test(timeout=10000)
    public void testRecursiveDig() throws IOException, InterruptedException {
        final String expected[] = new String[] {
                "- - 2      ",
                "- - 2      ",
                "2 2 1      ",
                "           ",
                "           ",
                "2 3 3 3 3 2",
                "- - - - - -"
        };

        Socket sock = TestUtil.connect(TestUtil.startServer(false, "board_file_2"));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            
            //throw away first line; look for BOARD msg.
            try {
                in.readLine();
            } catch (SocketTimeoutException ignored) { }
            
            out.println("dig 4 4");
            for(int i=0; i<expected.length; i++) {
                assertEquals(expected[i], in.readLine());
            }
        } catch (SocketTimeoutException e) {
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
