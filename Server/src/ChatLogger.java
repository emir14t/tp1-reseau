import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;



public class ChatLogger implements Logger {

    private final static int NUMBER_OF_LOGS = 15;
    private String filePath;

    ChatLogger(String path) {
        this.filePath = path;
    }


    String readFromFile() {

        try {
            Scanner scanner = new Scanner(new File(Logger.getCurrentWorkingDirectory() + filePath));
            int numLines = 0;
            while (scanner.hasNextLine()) {
                numLines++;
                scanner.nextLine();
            }
            scanner.close();

            String log = "";
            if (numLines < NUMBER_OF_LOGS) {
                log = String.join("\n", Files.readAllLines(Paths.get(Logger.getCurrentWorkingDirectory() + filePath)));
            } else {
                Scanner scanner2 = new Scanner(new File(Logger.getCurrentWorkingDirectory() + filePath));
                for (int i = 0; i < numLines - NUMBER_OF_LOGS; i ++) {
                    scanner2.nextLine();
                }
                for (int i = 0; i < NUMBER_OF_LOGS; i++) {
                    try {
                        log = log.concat(scanner2.nextLine() + "\n");
                    } catch (Exception e) {
                        System.out.println("Out of bounds");
                    }
                }
                scanner.close();
            }
            return log;

        } catch (IOException e) {
            System.out.println("Problem occured while reading file");
            return "";
        }
    }

    public void writeToLog(String msg) {
        Logger.writeToLog(this.filePath, msg);
    }
}
