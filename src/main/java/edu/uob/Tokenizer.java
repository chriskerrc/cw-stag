package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {

    String command;

    public Tokenizer(String command) {
        this.command = command;
    }

    public ArrayList<String> tokenizeCommand(){
        String[] stringArray = command.split(" ");
        return new ArrayList<>(Arrays.asList(stringArray));
    }


}
