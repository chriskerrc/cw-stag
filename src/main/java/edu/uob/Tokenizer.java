package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {

    String stagCommand;

    public Tokenizer(String stagCommand) {
        this.stagCommand = stagCommand;
    }

    public ArrayList<String> tokenizeCommand(){
        //replace common punctuation in command with a space
        String strippedCommand = stagCommand.replaceAll("[,.!?;]", " ");
        //split on one or more spaces
        String[] stringArray = strippedCommand.split("\\s+");
        //skip first token: name can have uppercase characters
        for(int tokenIndex = 1; tokenIndex < stringArray.length ; tokenIndex++){
            stringArray[tokenIndex] = stringArray[tokenIndex].toLowerCase();
        }
        return new ArrayList<>(Arrays.asList(stringArray));
    }
}
