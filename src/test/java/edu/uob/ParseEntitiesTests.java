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
    void testParseLocations() throws FileNotFoundException, ParseException {
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
    }



}
