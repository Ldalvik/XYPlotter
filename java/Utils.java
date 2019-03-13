package plotter;

import java.io.BufferedReader;
import java.io.FileReader;

public class Utils {

    public static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word) + word.length());
    }

    public static String removeAllAfter(String input, String word) {
        return input.substring(0, input.indexOf(word));
    }

    public static String removeFromTo(String input, String from, String to){
        return input.substring(input.indexOf(from), input.indexOf(to));
    }
    public static String removeBetween(String input, String firstWord, String secondWord) {
        return input.substring(input.indexOf(firstWord) + firstWord.length(),
                input.indexOf(secondWord));
    }
    private static String replace(String input, String word, String replace) {
        return input.replace(word, replace);
    }
}
