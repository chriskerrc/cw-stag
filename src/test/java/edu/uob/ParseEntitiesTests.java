package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import static org.junit.jupiter.api.Assertions.*;

public class ParseEntitiesTests {

    @Test
    void testParseBasicEntities() throws IOException, ParseException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        GameModel gameModel = server.gameModel;

        Graph wholeDocument = gameModel.parseEntities();
        gameModel.storeLocations(wholeDocument);

        //get cabin location
        Location cabin = gameModel.getLocationFromName("cabin");
        //check location name and description
        assertEquals("cabin", cabin.getName());
        assertEquals("A log cabin in the woods", cabin.getDescription());
        //check artefacts
        Artefact axe = (Artefact) cabin.getEntityFromName("axe");
        assertEquals("axe", axe.getName());
        assertEquals("A razor sharp axe", axe.getDescription());
        Artefact potion = (Artefact) cabin.getEntityFromName("potion");
        assertEquals("potion", potion.getName());
        assertEquals("Magic potion", potion.getDescription());
        //check furniture
        Furniture trapdoor = (Furniture) cabin.getEntityFromName("trapdoor");
        assertEquals("trapdoor", trapdoor.getName());
        assertEquals("Wooden trapdoor", trapdoor.getDescription());

        //get forest location
        Location forest = gameModel.getLocationFromName("forest");
        //check location name and description
        assertEquals("forest", forest.getName());
        assertEquals("A dark forest", forest.getDescription());
        //check artefacts
        Artefact key = (Artefact) forest.getEntityFromName("key");
        assertEquals("key", key.getName());
        assertEquals("Brass key", key.getDescription());
        //check potion isn't in forest
        Artefact nullPotion = (Artefact) forest.getEntityFromName("potion");
        assertNull(nullPotion);
        //check furniture
        Furniture tree = (Furniture) forest.getEntityFromName("tree");
        assertEquals("tree", tree.getName());
        assertEquals("A big tree", tree.getDescription());

        //get cellar location
        Location cellar = gameModel.getLocationFromName("cellar");
        //check location name and description
        assertEquals("cellar", cellar.getName());
        assertEquals("A dusty cellar", cellar.getDescription());
        //check characters
        Character elf = (Character) cellar.getEntityFromName("elf");
        assertEquals("elf", elf.getName());
        assertEquals("Angry Elf", elf.getDescription());
        //check key isn't in cellar
        Artefact nullKey = (Artefact) cellar.getEntityFromName("potion");
        assertNull(nullKey);

        //get storeroom location
        Location storeroom = gameModel.getLocationFromName("storeroom");
        //check location name and description
        assertEquals("storeroom", storeroom.getName());
        assertEquals("Storage for any entities not placed in the game", storeroom.getDescription());
        //check artefacts
        Artefact log = (Artefact) storeroom.getEntityFromName("log");
        assertEquals("log", log.getName());
        assertEquals("A heavy wooden log", log.getDescription());
        //check there are no characters
        assertTrue(storeroom.isNoCharacter());
        //check there is no furniture
        assertTrue(storeroom.isNoFurniture());

        //paths
        Location cabinDestination = gameModel.getDestinationFromLocation(cabin);
        assertEquals("forest", cabinDestination.getName());
        Location forestDestination = gameModel.getDestinationFromLocation(forest);
        assertEquals("cabin", forestDestination.getName());
        Location cellarDestination = gameModel.getDestinationFromLocation(cellar);
        assertEquals("cabin", cellarDestination.getName());
    }



}
