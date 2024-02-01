import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.FileWriter;

public interface Logger {


    static String getCurrentWorkingDirectory() {
        return Paths.get("").toAbsolutePath() + "/";
    }

    static void writeToLog(String filePath, String msg) {

        try {
            FileWriter fileWriter = new FileWriter(getCurrentWorkingDirectory() + filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            printWriter.println(msg);

            printWriter.close();
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("Failed to append to file");
        }
    }
}
