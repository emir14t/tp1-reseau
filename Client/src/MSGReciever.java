import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class MSGReciever extends Thread {
    private static Socket socket;

    private boolean shouldRun;

    public MSGReciever(Socket clientSocket) {
        socket = clientSocket;
        this.shouldRun = true;
    }

    public void terminate() {
        this.shouldRun = false;
    }

    @Override
    public void run() {
        while (this.shouldRun) {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String msg = in.readUTF();
                System.out.println(msg);
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                    System.out.println("Couldn't close socket");
                }

            }
        }
    }
}
