package protocol.fields;

import java.util.LinkedList;
import java.util.List;

public class PlayerField extends Field{
    byte sizeX;
    byte sizeY;
    byte sizeName;
    byte direction;
    byte killingCount;
    byte[] name;

    byte[] CoordinateX;
    byte[] CoordinateY;


    public PlayerField(byte id){
        super(id);
    }
    public PlayerField(byte id,byte sizeX,byte sizeY,byte sizeName,byte[] coordinateX,byte[] coordinateY,byte[] name,byte direction,byte killingCount){
        super(id);
        this.sizeX=sizeX;
        this.sizeY=sizeY;
        this.CoordinateX=coordinateX;
        this.CoordinateY=coordinateY;
        this.sizeName=sizeName;
        this.name =name;
        this.direction=direction;
        this.killingCount=killingCount;
    }

    @Override
    public List<Byte> getSize() {
        LinkedList<Byte> sizes=new LinkedList<>();
        sizes.add(sizeX);
        sizes.add(sizeY);
        sizes.add(sizeName);
        return sizes;
    }

    public Byte getDirection(){
        return direction;
    }

    public Byte getKillingCount(){
        return killingCount;
    }

    @Override
    public List<byte[]> getContent() {
        List<byte[]> content=new LinkedList<>();
        content.add(CoordinateX);
        content.add(CoordinateY);
        content.add(name);
        return content;
    }
}

