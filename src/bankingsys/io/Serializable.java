package bankingsys.io;

/**
 * Created by koallen on 29/3/18.
 */
public interface Serializable {
    void write(Serializer serializer);
    void read(Deserializer deserializer);
}
