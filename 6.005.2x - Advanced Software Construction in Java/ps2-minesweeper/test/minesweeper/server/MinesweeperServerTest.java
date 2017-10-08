/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */

package minesweeper.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import org.junit.Test;

/**
 * Tests all commands, i.e. FLAG, DEFLAG and DIG for all possible X and Y values.
 */
public class MinesweeperServerTest {

    /**
     * Testing strategy
     * ================
     * 
     * Tested all possible combinations of flag, deflag and dig, with concurrent
     * modifications by two players.
     */

    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 4000 + new Random().nextInt(1 << 15);

    private static final int MAX_CONNECTION_ATTEMPTS = 10;

    private static final String BOARDS_PKG = "minesweeper/server/";

    /**
     * Start a MinesweeperServer in debug mode with a board file from BOARDS_PKG.
     * 
     * Sourced from PublishedTest.
     * 
     * @param boardFile board to load
     * @return thread running the server
     * @throws IOException if the board file cannot be found
     */
    private static Thread startMinesweeperServer(String boardFile) throws IOException {
        final URL boardURL = ClassLoader.getSystemClassLoader().getResource(BOARDS_PKG + boardFile);
        if (boardURL == null) {
            throw new IOException("Failed to locate resource " + boardFile);
        }
        final String boardPath;
        try {
            boardPath = new File(boardURL.toURI()).getAbsolutePath();
        } catch (URISyntaxException urise) {
            throw new IOException("Invalid URL " + boardURL, urise);
        }
        final String[] args = new String[] {
                "--debug",
                "--port", Integer.toString(PORT),
                "--file", boardPath
        };
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Connect to a MinesweeperServer and return the connected socket.
     * 
     * Sourced from PublishedTest.
     * 
     * @param server abort connection attempts if the server thread dies
     * @return socket connected to the server
     * @throws IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server) throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(3000);
                return socket;
            } catch (ConnectException ce) {
                if ( ! server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }

    // covers FLAG -> DEFLAG -> DIG
    @Test(timeout = 10000)
    public void FlagDeflagDig() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player1 flags a square
        out1.println("flag 0 2");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("F - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("F - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 deflags the square
        out2.println("deflag 0 2");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        // player1 digs a bomb
        out1.println("dig 0 2");
        assertEquals("BOOM!", in1.readLine());
        out2.println("look");
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  1 2 - -", in2.readLine());
        assertEquals("    1 - -", in2.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }

    // covers FLAG -> DIG -> DEFLAG
    @Test(timeout = 10000)
    public void FlagDigDeflag() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 flags a square
        out2.println("flag 2 0");
        assertEquals("- - F - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - F - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        // player1 digs a bomb
        out1.println("dig 2 0");
        assertEquals("- - F - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - F - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 deflags the square
        out2.println("deflag 2 0");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }

    // covers DEFLAG -> FLAG -> DIG
    @Test(timeout = 10000)
    public void DeflagFlagDig() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player1 deflags a square
        out1.println("deflag 2 1");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 flags a square
        out2.println("flag 2 1");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - F - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - F - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        // player1 digs a bomb
        out1.println("dig 2 1");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - F - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - F - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }

    // covers DEFLAG -> DIG -> FLAG
    @Test(timeout = 10000)
    public void DeflagDigFlag() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 deflags a square
        out2.println("deflag 3 3");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        // player1 digs a bomb
        out1.println("dig 3 3");
        assertEquals("BOOM!", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - 3 1", in2.readLine());
        assertEquals("- 2 1 1  ", in2.readLine());
        assertEquals("- 1      ", in2.readLine());

        // player2 flags a square
        out2.println("flag 3 3");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - 3 1", in2.readLine());
        assertEquals("- 2 1 1  ", in2.readLine());
        assertEquals("- 1      ", in2.readLine());
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - 3 1", in1.readLine());
        assertEquals("- 2 1 1  ", in1.readLine());
        assertEquals("- 1      ", in1.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }

    // covers DIG -> FLAG -> DEFLAG
    @Test(timeout = 10000)
    public void DigFlagDeflag() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player1 digs a bomb
        out1.println("dig 4 0");
        assertEquals("BOOM!", in1.readLine());
        out2.println("look");
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2 1", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 flags a square
        out2.println("flag 4 0");
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2 1", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        out1.println("look");
        assertEquals("- - - 2  ", in1.readLine());
        assertEquals("- - - 2  ", in1.readLine());
        assertEquals("- - - 2 1", in1.readLine());
        assertEquals("- - - - -", in1.readLine());

        // player1 deflags a square
        out1.println("deflag 4 0");
        assertEquals("- - - 2  ", in1.readLine());
        assertEquals("- - - 2  ", in1.readLine());
        assertEquals("- - - 2 1", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2  ", in2.readLine());
        assertEquals("- - - 2 1", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }
    
    // covers DIG -> DEFLAG -> FLAG
    @Test(timeout = 10000)
    public void DigDeflagFlag() throws IOException {

        Thread thread = startMinesweeperServer("boardFile");

        Socket player1 = connectToMinesweeperServer(thread);
        Socket player2 = connectToMinesweeperServer(thread);

        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);

        // check HELLO message
        assertTrue("expected HELLO message", in1.readLine().startsWith("Welcome"));
        assertTrue("expected HELLO message", in2.readLine().startsWith("Welcome"));

        // check initial state
        out1.println("look");
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        assertEquals("- - - - -", in1.readLine());
        out2.println("look");
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());
        assertEquals("- - - - -", in2.readLine());

        // player2 digs a bomb
        out2.println("dig 0 2");
        assertEquals("BOOM!", in2.readLine());
        out1.println("look");
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  1 2 - -", in1.readLine());
        assertEquals("    1 - -", in1.readLine());

        // player1 flags a square
        out1.println("flag 0 2");
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  1 2 - -", in1.readLine());
        assertEquals("    1 - -", in1.readLine());
        out2.println("look");
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  1 2 - -", in2.readLine());
        assertEquals("    1 - -", in2.readLine());

        // player2 deflags a square
        out2.println("deflag 0 2");
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  2 - - -", in2.readLine());
        assertEquals("  1 2 - -", in2.readLine());
        assertEquals("    1 - -", in2.readLine());
        out1.println("look");
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  2 - - -", in1.readLine());
        assertEquals("  1 2 - -", in1.readLine());
        assertEquals("    1 - -", in1.readLine());

        out1.println("bye");
        out2.println("bye");
        player1.close();
        player2.close();
    }
}
