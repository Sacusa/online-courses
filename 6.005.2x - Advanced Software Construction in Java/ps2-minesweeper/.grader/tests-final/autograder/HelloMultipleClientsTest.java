package autograder;

import static autograder.TestUtil.regsp;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

public class HelloMultipleClientsTest {

    @Test(timeout=10000)
    public void testHelloMultipleClients() throws IOException, InterruptedException {
        final int THREADS = 10;
        
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();

        final ThreadWithObituary serverThread = TestUtil.startServer(false);

        Thread threads[] = new Thread[THREADS];

        class ConnectTestRunner implements Runnable {
            public void run() {
                try {
                    Socket sock = TestUtil.connect(serverThread);
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        String moo = in.readLine();
                        assertThat("(whitespace normalized)", regsp(moo), containsString("Welcome to Minesweeper"));
                    } finally {
                        sock.close();
                    }
                } catch (Throwable t) {
                    error.compareAndSet(null, t);
                }
            }
        }
        
        
        for(int i=0; i<THREADS; i++) {
            threads[i] = new Thread(new ConnectTestRunner());
        }
        for(int i=0; i<THREADS; i++) {
            threads[i].start();
        }
        for(int i=0; i<THREADS; i++) {
            threads[i].join();
        }
        if (error.get() != null) {
            throw new IOException("client encountered an error", error.get());
        }
    }
}
