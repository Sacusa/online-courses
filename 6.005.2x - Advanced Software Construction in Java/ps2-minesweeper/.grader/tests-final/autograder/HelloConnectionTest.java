package autograder;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.junit.Test;

public class HelloConnectionTest {

    /**
     * just check if we get any message from the server, don't care what it says.
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(timeout=10000)
    public void testHelloConnection() throws IOException, InterruptedException {
        Socket sock = TestUtil.connect(TestUtil.startServer(false));
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            in.readLine();
        } catch (SocketTimeoutException e) {
            fail("server timeout");
        } finally {
            sock.close();
        }
    }
}
