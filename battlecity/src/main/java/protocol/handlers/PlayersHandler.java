package protocol.handlers;

import protocol.MainPacket;
import protocol.fields.PlayerField;

import java.io.*;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

public class PlayersHandler {

    public static void toByteArray(List<PlayerField> listOfPlayers, ByteArrayOutputStream writer) {
        for (PlayerField field : listOfPlayers) {
            try {
                writer.write(field.getId());
                List<Byte> sizes = field.getSize();
                for (Byte size : sizes) {
                    writer.write(size);
                }
                writer.write(field.getDirection());
                writer.write(field.getKillingCount());
                List<byte[]> allContent = field.getContent();
                for (byte[] content : allContent) {
                    writer.write(content);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public static MainPacket parse(byte[] data) {
        MainPacket packet = MainPacket.create(data[3]);
        int offset = 4;
        while (true) {
            if (data.length - 3 <= offset) {
                return packet;
            }

            byte fieldId = data[offset];
            byte fieldSizeX = data[offset + 1];
            byte fieldSizeY = data[offset + 2];
            byte fieldSizeName = data[offset + 3];
            byte fieldDirection=data[offset+4];
            byte fieldKillingCount=data[offset+5];


            byte[] coordinateX = new byte[Byte.toUnsignedInt(fieldSizeX)];
            if (fieldSizeX != 0) {
                System.arraycopy(data, offset+6, coordinateX, 0, Byte.toUnsignedInt(fieldSizeX));
            }

            byte[] coordinateY = new byte[Byte.toUnsignedInt(fieldSizeY)];
            if (fieldSizeY != 0) {
                System.arraycopy(data, offset+6 + fieldSizeX, coordinateY, 0, Byte.toUnsignedInt(fieldSizeY));
            }

            byte[] name = new byte[Byte.toUnsignedInt(fieldSizeName)];
            if (fieldSizeName != 0) {
                System.arraycopy(data, offset+6 + fieldSizeX+fieldSizeY, name, 0, Byte.toUnsignedInt(fieldSizeName));
            }


            PlayerField field = new PlayerField(fieldId, fieldSizeX, fieldSizeY,fieldSizeName, coordinateX, coordinateY,name,fieldDirection,fieldKillingCount);
            packet.getPlayersCoordinates().add(field);

            offset += 6 + fieldSizeX + fieldSizeY+fieldSizeName;
        }
    }

    public static List<PlayerField> setValue(AbstractList<AbstractList<Object>> value) {
        List<PlayerField> players = new LinkedList<>();
        try {


            byte id = 0;
            for (List<Object> XY : value) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(XY.get(0));
                byte[] dataX = bos.toByteArray();
                if (dataX.length > 255) {
                    throw new IllegalArgumentException("Too much data sent");
                }

                ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                ObjectOutputStream oos1 = new ObjectOutputStream(bos1);
                oos1.writeObject(XY.get(1));
                byte[] dataY = bos1.toByteArray();
                if (dataY.length > 255) {
                    throw new IllegalArgumentException("Too much data sent");
                }

                ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
                ObjectOutputStream oos2 = new ObjectOutputStream(bos2);
                oos2.writeObject(XY.get(2));
                byte[] dataName = bos2.toByteArray();
                if (dataName.length > 255) {
                    throw new IllegalArgumentException("Too much data sent");
                }



                PlayerField field = new PlayerField(id++, (byte) dataX.length, (byte) dataY.length,(byte)dataName.length, dataX, dataY,dataName,(byte) XY.get(3),(byte) XY.get(4));
                players.add(field);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    public static <T> T getValue(List<PlayerField> players, Class<T> clazz) {
        try {
            List<List<Object>> allPlayers = new LinkedList<>();
            for (int i = 0; i < players.size(); i++) {
                List<Object> coordinates = new LinkedList<>();
                ByteArrayInputStream bis = new ByteArrayInputStream(players.get(i).getContent().get(0));
                ObjectInputStream ois = new ObjectInputStream(bis);
                coordinates.add((Object) ois.readObject());

                ByteArrayInputStream bis1 = new ByteArrayInputStream(players.get(i).getContent().get(1));
                ObjectInputStream ois1 = new ObjectInputStream(bis1);
                coordinates.add((Object) ois1.readObject());

                ByteArrayInputStream bis2 = new ByteArrayInputStream(players.get(i).getContent().get(2));
                ObjectInputStream ois2 = new ObjectInputStream(bis2);
                coordinates.add((Object) ois2.readObject());

                coordinates.add(players.get(i).getDirection());
                coordinates.add(players.get(i).getKillingCount());
                allPlayers.add(coordinates);
            }
            return (T) allPlayers;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
