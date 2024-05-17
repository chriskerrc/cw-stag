package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class InterpretExtendedActionsTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testExtendedActions() {
        //Pay elf
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        String response = sendCommandToServer("simon: look");
        System.out.println("what you see in the cabin " + response);
        assertTrue(response.contains("cabin"));

        /*
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        //check can see elf with description
        response = sendCommandToServer("simon: look");
        System.out.println("what you see in the cellar " + response);
        assertTrue(response.contains("angry looking elf"));
        response = sendCommandToServer("simon: pay elf");
        //check narration
        assertTrue(response.contains("You pay the elf your silver coin and he produces a shovel"));
        //check shovel is in cabin
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("shovel"));
        assertTrue(response.contains("sturdy"));
        sendCommandToServer("simon: get shovel");
        //check shovel is in inv
        response = sendCommandToServer("simon: inventory");
        assertTrue(response.contains("shovel"));

        //Bridge river to clearing
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: cut down tree with axe");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("heavy"));
        assertTrue(response.contains("log"));
        sendCommandToServer("simon: get log");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: get horn");
        //check you can't go to the clearing before it's produced
        response = sendCommandToServer("simon: goto clearing");
        assertTrue(response.contains("can't"));
        response = sendCommandToServer("simon: bridge river");
        //check narration
        assertTrue(response.contains("You bridge the river with the log and can now reach the other side"));
        sendCommandToServer("simon: goto clearing");
        //check you can see ground
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("soil"));
        assertTrue(response.contains("ground"));

        //Dig hole
        response = sendCommandToServer("simon: dig ground");
        //check narration
        assertTrue(response.contains("You dig into the soft ground and unearth a pot of gold !!!"));
        //check can't see ground anymore
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("soil"));
        assertFalse(response.contains("ground"));
        //check can see hole and gold
        assertTrue(response.contains("gold"));
        assertTrue(response.contains("big"));
        assertTrue(response.contains("pot"));
        assertTrue(response.contains("hole"));
        assertTrue(response.contains("deep"));
        //check you can get gold
        sendCommandToServer("simon: get gold");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("gold"));

       */

    }
}
