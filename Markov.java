import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

/**
 * Markov class to run StringChain
 * 
 * @author Catherine Wright Lab 6 - Markov Chain 101617696
 */

public class Markov {
    // only once after each word and white space
    private static final String WORD_REGEX = "(?<=\\b\\s)";
    // only once after each character
    private static final String CHAR_REGEX = "(?<=.)";

    /**
     * Main initialized stringchain, defines arguments, and creates the final
     * output
     */
    public static void main(String[] args) {
        int order = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);

        String regex = null;
        if (args[2].equalsIgnoreCase("char")) {
            regex = CHAR_REGEX;
        } else if (args[2].equalsIgnoreCase("word")) {
            regex = WORD_REGEX;
        }

        String delimiter = null;
        if (regex.equals(CHAR_REGEX)) {
            delimiter = "";
        } else if (regex.equals(WORD_REGEX)) {
            delimiter = " ";
        }

        StringChain stringchain = new StringChain(order);

        for (int i = 3; i < args.length; ++i) {
            String text = args[i];
            addFile(stringchain, regex, Paths.get(text));
        }

        Random rand = new Random();
        for (String str : stringchain.generate(count, rand)) {
            System.out.println(str + delimiter);
        }

        System.out.println();

    }

    /**
     * addfile reads the imported .txt and adds each word/char to our
     * stringchain, and checks for an IOException
     */
    private static void addFile(StringChain stringchain, String regex,
            Path text) {
        try (Scanner sc = new Scanner(text)) {
            sc.useDelimiter(regex);
            stringchain.addItems(sc);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
