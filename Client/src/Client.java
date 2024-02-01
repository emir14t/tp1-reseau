import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class Client {
    private static Socket socket;
    private static boolean shouldRun = true;
    private final static int BUFFER_SIZE = 200;

    private static String getInput(boolean isPassword) {
        if (isPassword) {
            Console console = System.console();
            char[] password = console.readPassword("Enter your password: ");
            if (password != null) {
                String passwordString = new String(password);
                return passwordString;
            }
            return "";
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                return reader.readLine();
            } catch (IOException e) {
                return "";
            }
        }
        
    }

    private static boolean sendMessage(String msg) {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(msg);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        
        IPVerifier IPverifier = new IPVerifier();
        String serverAddr = "";
        while (true) {
            System.out.print("Enter the server's IP address : ");
            serverAddr = getInput(false);

            if (IPverifier.verify(serverAddr, "\\."))
                break;
            else
                System.out.format("\"%s\" is not a valid IP address!\n", serverAddr);
        }

        int serverPort = 0;
        PortVerifier portVerifier = new PortVerifier();

        while (true) {
            System.out.print("Enter the server's port : ");
            String port = getInput(false);

            if (portVerifier.verify(port, "")) {
                serverPort = Integer.parseInt(port);
                break;
            }

            else
                System.out.format("\"%s\" is not a valid port number!\n", port);
        }

        // validate it :

        socket = new Socket(serverAddr, serverPort);
        System.out.format("Connected to %s:%d\n", serverAddr, serverPort);

        MSGReciever receiver = new MSGReciever(socket);   // async receive messages
        receiver.start();

        // send authentication
        System.out.print("Enter your username : ");
        String username = getInput(false);
        String password = getInput(true);

        sendMessage(username + ":" + password);

        while (shouldRun) {
            String msg;
            do {
                msg = getInput(false);
                if (msg.length() > BUFFER_SIZE )
                    System.out.format("Message is too long :\n expected length of %d, got %d\n", BUFFER_SIZE, msg.length());
                
                if (msg.length() < 1)
                    System.out.println("Can't send nothing!");
            } while (msg.length() > BUFFER_SIZE || msg.length() < 1);

            shouldRun = sendMessage(msg);
            if (shouldRun) {
                System.out.println("You : " + msg);
            }
        }

        socket.close();
        System.out.println("Disconnected");
        receiver.terminate();
        receiver.join();
    }
}
