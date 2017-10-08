package autograder;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

public class MinesweeperSystemTest {

    private static void read6Lines(BufferedReader in) throws IOException {
        for (int i = 0; i < 6; i++)
            in.readLine();
    }

    /**
     * hold a 3-client game with a series of interleaving operations.
     * 
     * 0 f 0 0 0 f
     * 0 0 0 f 0 0
     * 1 f 1 1 1 1
     * d 0 0 0 0 0
     * d 0 0 0 d 0
     * 1 0 0 d 0 0
     * 
     * - f - - - f
     * - - - f - -
     * - f - - - -
     * 2 3 3 3 3 2
     * 1 1        
     * - 1        
     * 
     * flag: 1,0   5,0   3,1   1,2
     * dig: 0,3   0,4   4,4   3,5
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(timeout=20000)
    public void systemTest() throws IOException, InterruptedException {
        final int THREADS = 3;
        
        final String expected[] = new String[] {
                "- F - - - F",
                "- - - F - -",
                "- F - - - -",
                "2 3 3 3 3 2",
                "1 1        ",
                "- 1        "
        };
        
        final ThreadWithObituary serverThread = TestUtil.startServer(false, "board_file_3");
        
        Thread threads[] = new Thread[THREADS];
        
        threads[0] = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket sock = TestUtil.connect(serverThread);
                    try {
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        in.readLine();
                        out.println("flag 1 0");
                        read6Lines(in);
                        out.println("dig 0 3");
                        read6Lines(in);
                        out.println("flag 5 0");
                        read6Lines(in);
                        out.println("dig 0 4");
                        read6Lines(in);
                        out.println("flag 3 1");
                        read6Lines(in);
                        out.println("bye");
                        out.close();
                        in.close();
                    } finally {
                        sock.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threads[1] = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket sock = TestUtil.connect(serverThread);
                    try {
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        in.readLine();
                        out.println("dig 4 4");
                        read6Lines(in);
                        out.println("flag 3 1");
                        read6Lines(in);
                        out.println("dig 3 5");
                        read6Lines(in);
                        out.println("flag 1 2");
                        read6Lines(in);
                        out.println("dig 0 3");
                        read6Lines(in);
                        out.println("bye");
                        out.close();
                        in.close();
                    } finally {
                        sock.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threads[2] = new Thread(new Runnable() {
            public void run() {
                try {
                    Socket sock = TestUtil.connect(serverThread);
                    try {
                        PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                        in.readLine();

                        out.println("flag 1 2");
                        read6Lines(in);
                        out.println("dig 0 4");
                        read6Lines(in);
                        out.println("flag 1 0");
                        read6Lines(in);
                        out.println("dig 4 4");
                        read6Lines(in);
                        out.println("flag 5 0");
                        read6Lines(in);
                        out.println("bye");
                        out.close();
                        in.close();
                    } finally {
                        sock.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        for(int i=0; i<THREADS; i++) {
            threads[i].start();
        }
        for(int i=0; i<THREADS; i++) {
            threads[i].join();
        }
        Thread.sleep(1000);
        Socket sock = TestUtil.connect(serverThread);
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter out = new PrintWriter(sock.getOutputStream(),true);
            out.println("look");
            //burn hello line
            in.readLine();

            for(int i=0; i<6; i++) {
                assertEquals(expected[i], in.readLine());
            }
        } finally {
            sock.close();
        }
    }
}
