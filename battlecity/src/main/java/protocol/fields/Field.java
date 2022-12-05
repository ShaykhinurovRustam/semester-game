package protocol.fields;

import java.util.List;

public abstract class Field {
    byte id;

    public Field(byte id){
        this.id=id;
    }

    public byte getId() {
        return id;
    }

    public abstract List<Byte> getSize();

    public abstract List<byte[]> getContent();
}
