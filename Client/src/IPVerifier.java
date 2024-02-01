
public class IPVerifier implements Verifier {
    static final int MAX_SIZE = 255;
    static final int NUM_OF_BYTES = 4;
    @Override
    public boolean verify(String msg, String regex) {
        try {
            int num;
            String[] ipAddr = msg.split(regex);
            if (ipAddr.length != NUM_OF_BYTES)
                return false;

            for (String addrByte : ipAddr) {
                num = Integer.parseInt(addrByte);
                if (num > MAX_SIZE || num < 0)
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
