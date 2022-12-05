package protocol.fields;

import java.util.LinkedList;
import java.util.List;

public class CoordinatesField extends Field{
    byte sizeX;
    byte sizeY;

    byte[] CoordinateX;
    byte[] CoordinateY;

    public CoordinatesField(byte id){
        super(id);
    }
    public CoordinatesField(byte id,byte sizeX,byte sizeY,byte[] coordinateX,byte[] coordinateY){
        super(id);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.CoordinateX=coordinateX;
        this.CoordinateY=coordinateY;
    }

    @Override
    public List<Byte> getSize() {
        LinkedList<Byte> sizes=new LinkedList<>();
        sizes.add(sizeX);
        sizes.add(sizeY);
        return sizes;
    }

    @Override
    public List<byte[]> getContent() {
        List<byte[]> content=new LinkedList<>();
        content.add(CoordinateX);
        content.add(CoordinateY);
        return content;
    }
}
