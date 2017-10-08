package autograder;

import static autograder.TestUtil.nosp;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * Make sure that if player 1 alters the board, player 2 seems the same change.
 * 
 * @author njoliat
 *
 */
public class SharedMemoryTest {

    @Test(timeout=10000)
    public void testSharedMemory() throws IOException, InterruptedException {
        final String expected[] = new String[] {
                "- - - - - -",
                "- F - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -",
                "- - - - - -"
        };

        final ThreadWithObituary serverThread = TestUtil.startServer(false, "board_file_2");

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(new Runnable() {public void run() {
            try {
                Socket sock = TestUtil.connect(serverThread);
                PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                out.println("flag 1 1");
                // Don't actually close this connection.
                latch.countDown();
            } catch (IOException ignored) {}
        }
        }).start();
        
        Thread.sleep(1000);
        latch.await(2, TimeUnit.SECONDS);
        
        Socket sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            String moo;

            //throw away first line; in.readLine();
            try {
                moo = in.readLine();
            } catch (SocketTimeoutException ignored) { }

            out.println("look");
            for(int i=0; i<expected.length; i++) {
                moo = in.readLine();
                assertEquals("(whitespace removed)", nosp(expected[i]), nosp(moo));
            }
        } finally {
            sock.close();
        }
    }
}
