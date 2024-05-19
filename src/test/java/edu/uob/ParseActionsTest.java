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
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseActionsTest {
    @Test
    void testParseBasicActions() throws IOException, ParserConfigurationException, SAXException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        GameModel gameModel = server.getGameModel();
        Document actionsDocument = gameModel.parseActions();
        gameModel.storeActions(actionsDocument);

        //First action
        //get hashset of gameActions from keyphrase "open"
        HashSet<GameAction> gameActionHashSetOpen = gameModel.getGameActionHashSet("open");
        GameAction gameAction = gameActionHashSetOpen.stream().findFirst().orElse(null);
        assert gameAction != null;
        //check subjects
        Subject trapdoorSubject = (Subject) gameAction.getSubjectEntity("trapdoor");
        assertEquals(trapdoorSubject.getName(), "trapdoor");
        Subject keySubject = (Subject) gameAction.getSubjectEntity("key");
        assertEquals(keySubject.getName(), "key");
        //check consumables
        Consumable keyConsumable = (Consumable) gameAction.getConsumedEntity("key");
        assertEquals(keyConsumable.getName(), "key");
        //check products
        Product cellarProduct = (Product)  gameAction.getProducedEntity("cellar");
        assertEquals(cellarProduct.getName(), "cellar");
        //check narration
        String firstActionNarration = gameAction.getNarration();
        assertEquals(firstActionNarration, "You unlock the trapdoor and see steps leading down into a cellar");
        //check that open and unlock keyphrases are mapped to same hashset of GameActions
        HashSet<GameAction> gameActionHashSetUnlock = gameModel.getGameActionHashSet("unlock");
        assertEquals(gameActionHashSetOpen, gameActionHashSetUnlock);

        //Second action
        //get hashset of gameActions from keyphrase "cut"
        HashSet<GameAction> gameActionHashSetCut = gameModel.getGameActionHashSet("cut");
        GameAction gameActionCut = gameActionHashSetCut.stream().findFirst().orElse(null);
        assert gameActionCut != null;
        //Check subjects
        Subject treeSubject = (Subject) gameActionCut.getSubjectEntity("tree");
        assertEquals(treeSubject.getName(), "tree");
        Subject axeSubject = (Subject) gameActionCut.getSubjectEntity("axe");
        assertEquals(axeSubject.getName(), "axe");

        //Third action
        //get hashset of gameActions from keyphrase "drink"
        HashSet<GameAction> gameActionHashSetDrink = gameModel.getGameActionHashSet("drink");
        GameAction gameActionDrink = gameActionHashSetDrink.stream().findFirst().orElse(null);
        assert gameActionDrink != null;
        //check consumables
        Consumable potionConsumable = (Consumable) gameActionDrink.getConsumedEntity("potion");
        assertEquals(potionConsumable.getName(), "potion");

        //Fourth action
        //get hashset of gameActions from keyphrase "attack"
        HashSet<GameAction> gameActionHashSetAttack = gameModel.getGameActionHashSet("attack");
        GameAction gameActionAttack = gameActionHashSetAttack.stream().findFirst().orElse(null);
        //check narration
        assert gameActionAttack != null;
        String fourthActionNarration = gameActionAttack.getNarration();
        assertEquals(fourthActionNarration, "You attack the elf, but he fights back and you lose some health");
    }
}
