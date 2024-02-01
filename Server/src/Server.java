import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;


public class Server {
    private static ServerSocket Listener;

    private static ArrayList<ClientHandler> clients;

    public static void main(String[] args) throws Exception {
        int clientNumber = 0;
        clients = new ArrayList<>();

        String serverAddress;
        IPVerifier IPverifier = new IPVerifier();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the IP address of the server : ");
            serverAddress = reader.readLine();

            if (IPverifier.verify(serverAddress, "\\."))
                break;
            else
                System.out.format("\"%s\" is not a valid IP address!\n", serverAddress);
        }

        int serverPort = -1;
        PortVerifier portVerifier = new PortVerifier();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the server's port of use : ");
            String port = reader.readLine();

            if (portVerifier.verify(port, "")) {
                serverPort = Integer.parseInt(port);
                break;
            }

            else
                System.out.format("\"%s\" is not a valid port number!\n", port);
        }

        Listener = new ServerSocket();
        Listener.setReuseAddress(true);

        InetAddress serverIP = InetAddress.getByName(serverAddress);
        Listener.bind(new InetSocketAddress(serverIP, serverPort));
        System.out.format("Server is running on %s:%d...\n", serverAddress, serverPort);
        
        PasswordLogger pwLogger = new PasswordLogger("users.txt", "pw.txt");

        try {
            while (true) {
                clients.add(new ClientHandler(Listener.accept(), clientNumber++, clients, "messages.txt", pwLogger));
                clients.get(clients.size() - 1).start();
            }
        } finally {
            Listener.close();
        }
    }
}
