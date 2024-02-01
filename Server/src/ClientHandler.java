import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final int clientNumber;
    private boolean shouldRun;

    private ChatLogger loggerMSG;

    private String username;

    private final ArrayList<ClientHandler> otherClients;

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("something went horribly wrong...");
        }
    }

    public ClientHandler(Socket socket, int clientNumber, ArrayList<ClientHandler> otherClients, String logPath, PasswordLogger loggerPW) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        this.shouldRun = true;
        this.username = "";
        System.out.format("New connection with client no. %d @ %s\n", this.clientNumber, this.socket.getInetAddress());
        
        this.loggerMSG = new ChatLogger(logPath);
        this.otherClients = otherClients;

        try {
            // get credentials
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String[] msg = in.readUTF().split(":");

            if (msg.length != 2) {
                this.closeConnection();
                return;
            }

            boolean isValid = loggerPW.verifyUser(msg[0], msg[1]);
            if (isValid) {
                this.username = msg[0];
                System.out.println("User authenticated");
                this.sendToClient(this.loggerMSG.readFromFile());
            } else {
                sendToClient("Invalid Username or Password!\nConnection terminated...");
                this.closeConnection();
            }
            
        } catch (IOException e) {
            System.out.println("Couldn't authenticate client");
            this.closeConnection();
        }
    }

    public void sendToClient (String msg) {
        try {
            DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
            output.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Couldn't send message!");
        }
    }

    @Override
    public void run() {

        while (this.shouldRun) {
            try {
                DataInputStream msg = new DataInputStream(this.socket.getInputStream());
                String message = msg.readUTF();
                
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH:mm:ss");
        
                System.out.format("New message from %s!\n", this.username);
                String msgToSend = "[" + this.username + " - " + this.socket.getInetAddress() + ":" + this.socket.getPort() + " - " + currentDateTime.format(formatter) + "] : " + message;
                this.loggerMSG.writeToLog(msgToSend);

                // send to other clients
                for (ClientHandler client : this.otherClients) {
                    if (client.clientNumber != this.clientNumber)
                        client.sendToClient(msgToSend);
                }

            } catch (IOException e) {
                this.shouldRun = false;
            }
        }
        this.closeConnection();
        otherClients.remove(this);

        System.out.format("Connection with %s terminated\n", this.username);
    }
}
