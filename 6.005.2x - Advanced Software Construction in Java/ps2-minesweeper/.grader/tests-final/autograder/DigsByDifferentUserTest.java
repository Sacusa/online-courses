package autograder;

import static autograder.TestUtil.nosp;
import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class DigsByDifferentUserTest {
    
    @Test(timeout=10000)
    public void testDigsByDifferentUsers() throws IOException, InterruptedException, BrokenBarrierException {
        final String expected[] = new String[] {
                "   ", // 0
                "1 1", // 1
                "- -", // 2
                "- 2", // 3
                "1 1", // 4
                "   ", // 5
                "   ", // 6
                "   ", // 7
                "1 1", // 8
                "- -", // 9
                "- -", // 10
                "- -", // 11
                "- -", // 12
                "- -", // 13
                "- -", // 14
                "- -", // 15
                "- -", // 16
                "- -", // 17
                "- 1", // 18
                "- -", // 19
        };
        
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final ThreadWithObituary serverThread = TestUtil.startServer(false, "board_file_6");
        final String[] coords = new String[] {
                "0 0",  // recursive dig in top section
                "1 3",  // non-recursive dig
                "1 5",  // recursive dig in middle section
                "1 7",  // recursive dig in middle section
                "1 18", // non-recursive dig
        };
        final CyclicBarrier barrier = new CyclicBarrier(coords.length + 1);
        final CountDownLatch dug = new CountDownLatch(coords.length);
        
        for (String coord : coords) {
            new Thread(() -> {
                try {
                    Socket sock = TestUtil.connect(serverThread);
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        in.readLine();
                        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                        out.print("dig " + coord);
                        barrier.await();
                        out.println();
                        Thread.yield();
                        in.readLine();
                        dug.countDown();
                        Thread.sleep(1000);
                    } finally {
                        sock.close();
                    }
                } catch (Throwable t) {
                    error.compareAndSet(null, t);
                }
            }).start();
        }
        
        Socket sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            in.readLine();
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            barrier.await();
            dug.await(2, TimeUnit.SECONDS);
            out.println("look");
            for (String expect : expected) {
                String actual = in.readLine();
                assertEquals("(whitespace removed)", nosp(expect), nosp(actual));
            }
        } finally {
            sock.close();
        }
        
        if (error.get() != null) {
            throw new IOException("client encountered an error", error.get());
        }
    }
}
