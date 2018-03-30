package bankingsys.io;

/**
 * Created by koallen on 29/3/18.
 */
public class Deserializer {
    private byte[] buffer;
    private int bufferPosition;

    public Deserializer(byte[] inputBuffer) {
        buffer = inputBuffer;
        bufferPosition = 0;
    }

    public char readChar() {
        char c = (char) buffer[bufferPosition];
        return c;
    }

    public int readInt() {
        int val = (buffer[bufferPosition] << 24) & 0xFF000000 |
                (buffer[bufferPosition + 1] << 16) & 0x00FF0000 |
                (buffer[bufferPosition + 2] << 8) & 0x0000FF00 |
                buffer[bufferPosition + 3] & 0x000000FF;
        bufferPosition += 4;
        return val;
    }

    public float readFloat() {
        int val = readInt();
        return Float.intBitsToFloat(val);
    }

    public String readString() {
        int stringLength = readInt();
        System.out.println(stringLength);
        byte[] stringBuffer = new byte[stringLength];
        System.arraycopy(buffer, bufferPosition, stringBuffer, 0, stringLength);
        String str = new String(stringBuffer);
        bufferPosition += stringLength;
        return str;
    }

    //private boolean isWithinBound(int len) {
    //    return bufferPosition + len < buffer.length ? true : false;
    //}
}
