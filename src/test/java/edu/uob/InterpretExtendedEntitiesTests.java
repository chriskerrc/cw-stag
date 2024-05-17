package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

public class InterpretExtendedEntitiesTests {

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
    void testStoreExtendedLocations() {

        //check you can go from cabin to forest to cabin

    }

    @Test
    void testTraversePathsAndLook() {
        String response =  sendCommandToServer("simon: look");
        //check can see coin in cabin
        assertTrue(response.contains("coin"));
        sendCommandToServer("simon: goto forest");
        response =  sendCommandToServer("simon: look");
        //check descriptions of entities are included in look
        assertTrue(response.contains("rusty"));
        assertTrue(response.contains("pine"));
        //check can go to riverbank
        sendCommandToServer("simon: goto riverbank");
        //check you can see expected entities in riverbank
        response =  sendCommandToServer("simon: look");
        assertTrue(response.contains("grassy"));
        assertTrue(response.contains("horn"));
        assertTrue(response.contains("river"));
        //check can return to forest
        sendCommandToServer("simon: goto forest");
        response =  sendCommandToServer("simon: look");
        assertTrue(response.contains("rusty"));
        assertTrue(response.contains("pine"));
    }
}
