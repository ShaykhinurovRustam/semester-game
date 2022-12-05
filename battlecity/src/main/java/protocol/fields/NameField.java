package protocol.fields;

import java.util.LinkedList;
import java.util.List;

public class NameField extends Field{

    byte size;
    byte[] name;

    public NameField(byte id){
        super(id);
    }

    public NameField(byte id,byte size,byte[] name){
        super(id);
        this.size=size;
        this.name=name;
    }

    @Override
    public List<Byte> getSize() {
        LinkedList<Byte> sizes=new LinkedList<>();
        sizes.add(size);
        return sizes;
    }

    @Override
    public List<byte[]> getContent() {
        List<byte[]> content=new LinkedList<>();
        content.add(name);
        return content;
    }


}
