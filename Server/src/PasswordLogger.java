import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PasswordLogger implements Logger {
    
    private String userFilePath;
    private String pwFilePath;

    public PasswordLogger(String userFilePath, String pwFilePath) {
        this.userFilePath = userFilePath;
        this.pwFilePath = pwFilePath;
    }

    private class Pair {
        public int lineNumber;
        public boolean isInFile;

        public Pair(int num, boolean found) {
            this.lineNumber = num;
            this.isInFile = found;
        }
    };

    private Pair findInFile (String sequence, String filePath) {
        Pair result = new Pair(0, false);
        try {
            Scanner scanner = new Scanner(new File(Logger.getCurrentWorkingDirectory() + filePath));
            int lineNumber = 0;
            while (scanner.hasNextLine()) {
                lineNumber ++;
                if (scanner.nextLine().toString().equals(sequence) ) {
                    result.isInFile = true;
                    result.lineNumber = lineNumber;
                    return result;
                }
            }
            scanner.close();
        } catch (IOException e) {
            return result;
        }
        return result;
    }

    private Pair userExists(String username) {
        return findInFile(username, this.userFilePath);
    }

    private Pair pwConcords(String password, int lineNumber) {
        Pair result = findInFile(password, this.pwFilePath);

        if (result.lineNumber == lineNumber)
            result.isInFile = true;
        else
            result.isInFile = false;

        return result;
    }

    public boolean verifyUser(String username, String password) {
        Pair user = this.userExists(username);
        if (user.isInFile) {
            return this.pwConcords(password, user.lineNumber).isInFile;
        }
        createUser(username, password);
        return true;
    }

    public void createUser(String username, String password) {
        this.writeToLog(username, this.userFilePath);
        this.writeToLog(password, this.pwFilePath);
    }

    private void writeToLog(String msg, String filePath) {
        Logger.writeToLog(filePath, msg);
    }

}
