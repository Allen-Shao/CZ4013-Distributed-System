package bankingsys.io;

/**
 * Class for serialization
 */
public class Serializer {
    private byte[] buffer; // maintains the byte array
    private int bufferLength; // maintains the current buffer position

    /**
     * Creates a serializer with an empty internal buffer
     */
    public Serializer() {
        buffer = new byte[4];
        bufferLength = 0;
    }

    /**
     * Serialize a char into the buffer
     * @param c Char to be serialized
     */
    public void writeChar(char c) {
        extendBuffer(1);
        buffer[bufferLength] = (byte) (c & 0xFF);
        bufferLength += 1;
    }

    /**
     * Serialize an integer into the buffer
     * @param input Integer to be serialized
     */
    public void writeInt(int input) {
        extendBuffer(4);
        buffer[bufferLength] = (byte) (input >> 24 & 0xFF);
        buffer[bufferLength + 1] = (byte) (input >> 16 & 0xFF);
        buffer[bufferLength + 2] = (byte) (input >> 8 & 0xFF);
        buffer[bufferLength + 3] = (byte) (input & 0xFF);
        bufferLength += 4;
    }

    /**
     * Serialize a float into the buffer
     * @param input Float to be serialized
     */
    public void writeFloat(float input) {
        int inputInInt = Float.floatToIntBits(input);
        writeInt(inputInInt);
    }

    /**
     * Serialize a string into the buffer
     * @param input String to be serialized
     */
    public void writeString(String input) {
        writeInt(input.length());
        extendBuffer(input.length());
        byte[] stringByte = input.getBytes();
        System.arraycopy(stringByte, 0, buffer, bufferLength, input.length());
        bufferLength += input.length();
    }

    /**
     * Return the internal buffer
     * @return Internal buffer
     */
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * Return the length of the internal buffer
     * @return Length of the internal buffer
     */
    public int getBufferLength() {
        return bufferLength;
    }

    /**
     * Increase the buffer size if necessary
     * @param len Number of bytes needed
     */
    private void extendBuffer(int len) {
        int newLength = bufferLength + len;
        if (newLength > buffer.length) {
            byte[] newBuffer = new byte[newLength];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }
}
