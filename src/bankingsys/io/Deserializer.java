package bankingsys.io;

/**
 * Class for deserialization
 */
public class Deserializer {
    private byte[] buffer;
    private int bufferPosition;

    /**
     * Create a deserializer with a byte array
     * @param inputBuffer Bytes to be deserialized
     */
    public Deserializer(byte[] inputBuffer) {
        buffer = inputBuffer;
        bufferPosition = 0;
    }

    /**
     * Deserialize a char from the buffer
     * @return Deserialized char
     */
    public char readChar() {
        char c = (char) buffer[bufferPosition];
        bufferPosition += 1;
        return c;
    }

    /**
     * Deserialize an integer from the buffer
     * @return Deserialized integer
     */
    public int readInt() {
        int val = (buffer[bufferPosition] << 24) & 0xFF000000 |
                (buffer[bufferPosition + 1] << 16) & 0x00FF0000 |
                (buffer[bufferPosition + 2] << 8) & 0x0000FF00 |
                buffer[bufferPosition + 3] & 0x000000FF;
        bufferPosition += 4;
        return val;
    }

    /**
     * Deserialize a float from the buffer
     * @return Deserialized float
     */
    public float readFloat() {
        int val = readInt();
        return Float.intBitsToFloat(val);
    }

    /**
     * Deserialize a string from the buffer
     * @return Deserialized string
     */
    public String readString() {
        int stringLength = readInt();
        byte[] stringBuffer = new byte[stringLength];
        System.arraycopy(buffer, bufferPosition, stringBuffer, 0, stringLength);
        String str = new String(stringBuffer);
        bufferPosition += stringLength;
        return str;
    }
}
