package protocol.handlers;

import protocol.MainPacket;
import protocol.fields.Field;
import protocol.fields.ShotField;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class ShotHandler {


    public static void toByteArray(Field field,ByteArrayOutputStream writer){
        try {
            writer.write(field.getId());
            ShotField field1 = (ShotField) field;
            List<Byte> sizes = field1.getSize();
            for (Byte size : sizes) {
                writer.write(size);
            }
            List<byte[]> allContent = field1.getContent();
            for (byte[] content : allContent) {
                writer.write(content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static MainPacket parse(byte[] data) {
        MainPacket packet = MainPacket.create(data[3]);
        byte fieldId = data[4];
        byte fieldSizeX = data[5];
        byte fieldSizeY = data[6];

        byte[] coordinateX = new byte[Byte.toUnsignedInt(fieldSizeX)];
        if (fieldSizeX != 0) {
            System.arraycopy(data, 7, coordinateX, 0, Byte.toUnsignedInt(fieldSizeX));
        }

        byte[] coordinateY = new byte[Byte.toUnsignedInt(fieldSizeY)];
        if (fieldSizeY != 0) {
            System.arraycopy(data, 7 + fieldSizeX, coordinateY, 0, Byte.toUnsignedInt(fieldSizeY));
        }
        byte direction=data[fieldSizeX+fieldSizeY+7];
        packet.field = new ShotField(fieldId, fieldSizeX, fieldSizeY, coordinateX, coordinateY,direction);

        return packet;
    }


    public static Field setValue(java.util.AbstractList<Integer> value) {
        Field field;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(value.get(0));
            byte[] dataX = bos.toByteArray();
            if (dataX.length > 255) {
                throw new IllegalArgumentException("Too much data sent");
            }

            ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
            ObjectOutputStream oos1 = new ObjectOutputStream(bos1);
            oos1.writeObject(value.get(1));
            byte[] dataY = bos1.toByteArray();
            if (dataY.length > 255) {
                throw new IllegalArgumentException("Too much data sent");
            }

            byte direction=0;
            switch(value.get(2)){
                case 1:
                    direction=1;
                    break;
                case 2:
                    direction=2;
                    break;
                case 3:
                    direction=3;
                    break;
                case 4:
                    direction=4;
            }

            field = new ShotField((byte) 1, (byte) dataX.length, (byte) dataY.length, dataX, dataY,direction);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    public static <T> T getValue(Field field, Class<T> clazz) {
        try {
            List<Integer> coordinates = new LinkedList<>();
            List<byte[]> XYAndDirect = field.getContent();
            ByteArrayInputStream bis = new ByteArrayInputStream(XYAndDirect.get(0));
            ObjectInputStream ois = new ObjectInputStream(bis);
            coordinates.add((Integer) ois.readObject());

            bis = new ByteArrayInputStream(XYAndDirect.get(1));
            ois = new ObjectInputStream(bis);
            coordinates.add((Integer) ois.readObject());
            coordinates.add((int) XYAndDirect.get(2)[0]);
            return (T) coordinates;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
