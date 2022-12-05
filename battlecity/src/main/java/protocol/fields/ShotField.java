package protocol.fields;

import java.util.LinkedList;
import java.util.List;

public class ShotField extends Field{
    byte sizeX;
    byte sizeY;
    byte direction;

    byte[] CoordinateX;
    byte[] CoordinateY;

    public ShotField(byte id){
        super(id);
    }
    public ShotField(byte id,byte sizeX,byte sizeY,byte[] coordinateX,byte[] coordinateY,byte direction){
        super(id);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.CoordinateX=coordinateX;
        this.CoordinateY=coordinateY;
        this.direction=direction;
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
        content.add(new byte[]{direction});
        return content;
    }
}
