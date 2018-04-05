package bankingsys.io;

/**
 * Interface to be implemented by class who needs serialization/deserialization
 */
public interface Serializable {
    /**
     * Serialize member variables into a buffer
     * @param serializer Serializer used
     */
    void write(Serializer serializer);

    /**
     * Deserialize member variables from a buffer
     * @param deserializer Deserializer used
     */
    void read(Deserializer deserializer);
}
