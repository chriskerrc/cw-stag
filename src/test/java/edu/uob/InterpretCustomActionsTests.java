package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class InterpretCustomActionsTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "custom-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "custom-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testInterpretShutTrapdoor() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: get bolt");
        //check you can pick up bolt
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("bolt"));
        sendCommandToServer("simon: goto cabin");
        //check you can't see path to cellar before trapdoor unlocked
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("cellar"));
        //check you can't go to cellar before trapdoor is open
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("can't get there from here"));
        response = sendCommandToServer("simon: open trapdoor");
        //check narration
        assertEquals(response, "You unlock the trapdoor and see steps leading down into a cellar");
        //check can see path to cellar now
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));
        //check you can go to cellar
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("Angry Elf"));
        //return to cabin
        sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: shut trapdoor");
        assertEquals("You lock the trapdoor. The bolt seems to be stuck.", response);
        //check you can't see path to cellar anymore
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("cellar"));
        //check you can't go to cellar anymore
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("can't"));
    }
}
