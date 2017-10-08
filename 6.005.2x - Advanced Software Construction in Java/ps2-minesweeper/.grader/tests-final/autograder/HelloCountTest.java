package autograder;

import static autograder.TestUtil.regsp;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class HelloCountTest {
    
    /**
     * open several client connections, check the N in hello msg on the last one.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(timeout=10000)
    public void testHelloCount() throws IOException, InterruptedException {
        final int THREADS = 3;

        final ThreadWithObituary serverThread = TestUtil.startServer(false);

        Thread threads[] = new Thread[THREADS];
        final CountDownLatch latch = new CountDownLatch(THREADS);

        for(int i=0; i<THREADS; i++) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    try {
                        Socket sock = TestUtil.connect(serverThread);
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        String moo = in.readLine();
                        assertTrue(moo != null);
                        // Don't actually close this connection.
                        latch.countDown();
                    } catch (IOException ignored) {}
                }
            });
        }
        for(int i=0; i<THREADS; i++) {
            threads[i].start();
        }
        
        latch.await(2, TimeUnit.SECONDS);
        
        Socket sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String moo = in.readLine();
            org.junit.Assert.assertThat("(whitespace normalized)", regsp(moo), containsString("Players: " + (THREADS+1)));
        } catch (SocketTimeoutException e) {
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
