package edu.uob;

import org.junit.jupiter.api.Test;
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
    void testParseBasicEntities() throws FileNotFoundException, ParseException {
        GameModel gameModel = new GameModel();
        Graph wholeDocument = gameModel.parseEntities();
        gameModel.storeLocations(wholeDocument);

        //get cabin location
        Location cabin = gameModel.getLocationFromName("cabin");
        //check location name and description
        assertEquals("cabin", cabin.getName());
        assertEquals("A log cabin in the woods", cabin.getDescription());
        //check artefacts
        Artefact axe = cabin.getArtefactFromName("axe");
        assertEquals("axe", axe.getName());
        assertEquals("A razor sharp axe", axe.getDescription());
        Artefact potion = cabin.getArtefactFromName("potion");
        assertEquals("potion", potion.getName());
        assertEquals("Magic potion", potion.getDescription());
        //check furniture
        Furniture trapdoor = cabin.getFurnitureFromName("trapdoor");
        assertEquals("trapdoor", trapdoor.getName());
        assertEquals("Wooden trapdoor", trapdoor.getDescription());

        //get forest location
        Location forest = gameModel.getLocationFromName("forest");
        //check location name and description
        assertEquals("forest", forest.getName());
        assertEquals("A dark forest", forest.getDescription());
        //check artefacts
        Artefact key = forest.getArtefactFromName("key");
        assertEquals("key", key.getName());
        assertEquals("Brass key", key.getDescription());
        //check potion isn't in forest
        Artefact nullPotion = forest.getArtefactFromName("potion");
        assertNull(nullPotion);
        //check furniture
        Furniture tree = forest.getFurnitureFromName("tree");
        assertEquals("tree", tree.getName());
        assertEquals("A big tree", tree.getDescription());

        //get cellar location
        Location cellar = gameModel.getLocationFromName("cellar");
        //check location name and description
        assertEquals("cellar", cellar.getName());
        assertEquals("A dusty cellar", cellar.getDescription());
        //check characters
        Character elf = cellar.getCharacterFromName("elf");
        assertEquals("elf", elf.getName());
        assertEquals("Angry Elf", elf.getDescription());
        //check key isn't in cellar
        Artefact nullKey = cellar.getArtefactFromName("potion");
        assertNull(nullKey);
    }



}
