package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;

public class InterpretBuiltInActionsTests {

    private GameServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    void testInterpretOpenTrapdoor() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        //check you can't see path to cellar before trapdoor unlocked
        String response = sendCommandToServer("simon: look");
        assertFalse(response.contains("cellar"));
        //check you can't go to cellar before trapdoor is open
        response = sendCommandToServer("simon: goto cellar");
        assertTrue(response.contains("can't get there from here"));
        response = sendCommandToServer("simon: open trapdoor");
        //check narration
        assertEquals(response, "You unlock the trapdoor and see steps leading down into a cellar");
        //check key is not in inventory
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("key"));
        //check key is in storeroom
        GameModel gameModel = server.getGameModel();
        Location storeroom = gameModel.getLocationFromName("storeroom");
        assertNotNull(storeroom.getEntityFromName("key"));
        //check can see path to cellar now
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("cellar"));
        //check you can go to cellar
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("Angry Elf"));
    }

    @Test
    void testInterpretChopTree() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: chop tree");
        //check narration
        assertEquals(response, "You cut down the tree with the axe");
        response = sendCommandToServer("simon: look");
        //check that tree is gone from forest
        assertFalse(response.contains("tree"));
        //check that the log has appeared in the forest
        assertTrue(response.contains("A heavy wooden log"));
    }

    @Test
    void testInterpretDrinkPotionFromLocation() {
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("potion"));
        response = sendCommandToServer("simon: drink potion");
        //check narration
        assertEquals(response, "You drink the potion and your health improves");
        sendCommandToServer("simon: look");
        //check that the potion is gone from the cabin
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("potion"));
    }

    @Test
    void testInterpretDrinkPotionFromInventory() {
        sendCommandToServer("simon: get potion");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("potion"));
        response = sendCommandToServer("simon: drink potion");
        //check narration
        assertEquals(response, "You drink the potion and your health improves");
        //check that potion is gone from inventory
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("potion"));
    }
}
