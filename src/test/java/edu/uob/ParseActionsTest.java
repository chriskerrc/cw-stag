package edu.uob;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseActionsTest {
    @Test
    void testParseBasicActions() throws IOException, ParserConfigurationException, SAXException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        GameModel gameModel = server.gameModel;
        Document actionsDocument = gameModel.parseActions();
        gameModel.storeActions(actionsDocument);
        //get hashset of gameActions from keyphrase

        //check the first action

        //check the first keyphrase

        //check subjects

        //check products

        //check consumables

        //check narration


    }
}
