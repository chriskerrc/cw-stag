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
        //split on one or more spaces after the colon
        ArrayList<String> commandTokens = new ArrayList<>();
        int colonIndex = strippedCommand.indexOf(":");
        //if there is a colon in the command
        if(colonIndex != -1){
            //add name
            commandTokens.add(strippedCommand.substring(0, colonIndex).trim());
            String commandAfterName = strippedCommand.substring(colonIndex + 1);
            String[] tokensAfterName = commandAfterName.split("\\s+");
            //make all tokens after name lowercase
            for(String commandToken : tokensAfterName){
                commandTokens.add(commandToken.toLowerCase());
            }
            commandTokens.addAll(Arrays.asList(tokensAfterName));
        }
        //if there's no colon, the command is invalid
        else{
            return null;
        }
        return commandTokens;
    }
}
