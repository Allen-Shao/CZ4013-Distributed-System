package bankingsys.io;

/**
 * Created by koallen on 29/3/18.
 */
public class Serializer {
    private byte[] buffer; // maintains the byte array
    private int bufferLength; // maintains the current buffer position

    public Serializer() {
        buffer = new byte[4];
        bufferLength = 0;
    }

    public void writeChar(char c) {
        extendBuffer(1);
        buffer[bufferLength] = (byte) (c & 0xFF);
        bufferLength += 1;
    }

    public void writeInt(int input) {
        extendBuffer(4);
        buffer[bufferLength] = (byte) (input >> 24 & 0xFF);
        buffer[bufferLength + 1] = (byte) (input >> 16 & 0xFF);
        buffer[bufferLength + 2] = (byte) (input >> 8 & 0xFF);
        buffer[bufferLength + 3] = (byte) (input & 0xFF);
        bufferLength += 4;
    }

    public void writeFloat(float input) {
        int inputInInt = Float.floatToIntBits(input);
        writeInt(inputInInt);
    }

    public void writeString(String input) {
        writeInt(input.length());
        extendBuffer(input.length());
        byte[] stringByte = input.getBytes();
        System.arraycopy(stringByte, 0, buffer, bufferLength, input.length());
        bufferLength += input.length();
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    /**
     * Increase the buffer size if necessary
     * @param len
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
