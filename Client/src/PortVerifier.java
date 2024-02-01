public class PortVerifier implements Verifier {

    public static final int MINIMUM_PORT_NUMBER = 5000;
    public static final int MAXIMUM_PORT_NUMBER = 5050;
    
    @Override
    public boolean verify(String msg, String regex) {
        int portNumber = Integer.parseInt(msg);
        return portNumber >= MINIMUM_PORT_NUMBER && portNumber <= MAXIMUM_PORT_NUMBER;
    }

    
}
