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

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseEntitiesTests {

    @Test
    void testParseLocations() throws FileNotFoundException, ParseException {
        GameModel gameModel = new GameModel();
        Graph wholeDocument = gameModel.parseEntities();
        gameModel.storeLocations(wholeDocument);
        gameModel.printLocations();
    }

    @Test
    void testParseArtefacts() throws FileNotFoundException, ParseException {
        GameModel gameModel = new GameModel();
        Graph wholeDocument = gameModel.parseEntities();
        Location dummyLocation = new Location("cabin", "A log cabin in the woods");
        gameModel.storeArtefacts(wholeDocument, dummyLocation);
    }


}
