import java.lang.String;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.io.IOException;

public class OpenSSG {

    static final String DIST_FOLDER = "./dist";
    private static final String OPTION_DESCRIPTION = "\nAvailable options:\n[-v | --version]\t\t\tDisplay program information\n[-h | --help]\t\t\t\tDisplay how to use options\n[-i | --input <file-or-folder>]\t\tSpecify input file or folder\n[-o | --output <folder-name>]\t\tSpecify output folder. Default is ./dist\n[-s | --stylesheet <CSS-URL>]\t\tAdd CSS links to each of the html file";

    public static void main(String[] args) throws IOException {
        if (areArgsValid(args)) {
            switch (args[0]) {
                case "--version":
                case "-v":
                    Release release = new Release();
                    System.out.println("OpenSSG version " + release.version + ", " + release.dateOfRelease);
                    break;
                case "--help":
                case "-h":
                    System.out.println("usage: " + new Release().name + " <option>\n" + OPTION_DESCRIPTION);
                    break;
                default:
                    var optionArgs = analyzeArgs(args);
                    sortOptionAndCreateFiles(optionArgs);
            }
        }
    }

    public static Dictionary<String, Object> analyzeArgs(String[] args) {
        Dictionary<String, Object> optionArgs = new Hashtable<String, Object>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-i") || args[i].equals("--input")) {
                optionArgs.put("-i", args[i + 1]);
            } else if (args[i].equals("-o") || args[i].equals("--output")) {
                optionArgs.put("-o", args[i + 1]);
            } else if (args[i].equals("-s") || args[i].equals("--stylesheet")) {
                int j = i + 1;
                ArrayList<String> stylesheetLinks = new ArrayList<String>();

                while (j < args.length && !args[j].startsWith("-")) {
                    stylesheetLinks.add(args[j]);
                    j++;
                }
                optionArgs.put("-s", stylesheetLinks);
            }
        }
        return optionArgs;
    }

    public static boolean areArgsValid(String[] args) {
        boolean isValid = true;
        String[] singleArgOptions = { "-i", "--input", "-o", "--output" };
        String[] stylesheetOptions = { "-s", "--stylesheet" };

        if (args.length > 0) {
            if (!(Arrays.asList(args).contains("-i") || Arrays.asList(args).contains("-input"))){
                isValid = false;
                System.out.println("Input option must be provided with arguement");
            } else {
                for (int i = 0; i < args.length; i++) {
                    if (Arrays.asList(singleArgOptions).contains(args[i])) {
                        i++;
                        if (i<args.length){
                            if (args[i].startsWith(("-"))){
                                isValid = false;
                                System.out.println("Missing option argument");
                            }
                        } else {
                            isValid = false;
                            System.out.println("Missing option argument");
                        }
                    } else if (Arrays.asList(stylesheetOptions).contains(args[i])){
                        i++;
                        if (i<args.length){
                            while(i<args.length && !args[i].startsWith("-")){
                                i++;
                            }
                        } else {
                            isValid = false;
                            System.out.println("Missing option argument. Please provide CSS links to add.");
                        }
                    }
                }
            }
        } else {
            System.out.println("Please provide an option. You can check usage by running OpenSSG -h or OpenSSG --help.");
        }
        return isValid;
    }

    public static void sortOptionAndCreateFiles(Dictionary<String, Object> optionArgs) throws IOException {
        String inputOption = (String) optionArgs.get("-i");
        String outputOption = "";

        if (optionArgs.get("-o") != null && optionArgs.get("-s") != null) {
            outputOption = (String) optionArgs.get("-o");
            @SuppressWarnings("unchecked")
            ArrayList<String> stylesheetOption = (ArrayList<String>) optionArgs.get("-s");

            createHTMLFiles(inputOption, outputOption, stylesheetOption);

        } else if (optionArgs.get("-o") == null && optionArgs.get("-s") != null) {
            outputOption = DIST_FOLDER;
            @SuppressWarnings("unchecked")
            ArrayList<String> stylesheetOption = (ArrayList<String>) optionArgs.get("-s");

            createHTMLFiles(inputOption, outputOption, stylesheetOption);

        } else if (optionArgs.get("-o") != null && optionArgs.get("-s") == null) {
            outputOption = (String) optionArgs.get("-o");

            createHTMLFiles(inputOption, outputOption);
        } else if (optionArgs.get("-o") == null && optionArgs.get("-s") == null) {

            createHTMLFiles(inputOption);

        }
    }

    public static void createHTMLFiles(String inputArg) throws IOException {
        FileUtilities fileUtilities = new FileUtilities();

        Path outPath = Paths.get(DIST_FOLDER);
        if (Files.exists(outPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
            fileUtilities.removeExistingFolder(DIST_FOLDER);
        }

        Files.createDirectories(outPath);

        ArrayList<String> txtFiles = fileUtilities.getAllTxtFiles(inputArg);
        fileUtilities.generateHTMLFiles(txtFiles);
    };

    public static void createHTMLFiles(String input, String output) throws IOException {
        FileUtilities fileUtilities = new FileUtilities();

        Path outPath = Paths.get(output);
        if (Files.exists(outPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
            fileUtilities.removeExistingFolder(output);
        }

        Files.createDirectories(outPath);

        ArrayList<String> txtFiles = fileUtilities.getAllTxtFiles(input);
        fileUtilities.generateHTMLFiles(txtFiles, output);
    }

    public static void createHTMLFiles(String input, String output, ArrayList<String> stylesheetLinks)
            throws IOException {
        FileUtilities fileUtilities = new FileUtilities();

        Path outPath = Paths.get(output);
        if (Files.exists(outPath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
            fileUtilities.removeExistingFolder(output);
        }

        Files.createDirectories(outPath);

        ArrayList<String> txtFiles = fileUtilities.getAllTxtFiles(input);
        fileUtilities.generateHTMLFiles(txtFiles, output, stylesheetLinks);
    }
}
