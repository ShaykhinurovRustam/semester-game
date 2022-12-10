package protocol;

import protocol.fields.Field;
import protocol.fields.PlayerField;
import protocol.handlers.*;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class MainPacket {
    private static final byte HEADER_1 = (byte) 0x18;
    private static final byte HEADER_2 = (byte) 0xe5;
    private static final byte HEADER_3 = (byte) 0xba;

    private static final byte FOOTER_1 = (byte) 0xfc;
    private static final byte FOOTER_2 = (byte) 0xc1;
    private static final byte FOOTER_3 = (byte) 0xca;

    private static final byte Name = 1;
    private static final byte Coordinates = 2;
    private static final byte Shot = 3;
    private static final byte Exit = 4;
    private static final byte Players = 5;
    private static final byte BusyName = 6;


    byte type;

    public byte getType() {
        return type;
    }

    public static boolean compareEOP(byte[] arr, int lastItem) {
        return arr[lastItem - 2] == FOOTER_1 && arr [lastItem-1] == FOOTER_2 && arr [lastItem] == FOOTER_3;
    }


    public Field field;
    List<PlayerField> PlayersCoordinates = new LinkedList<>();


    public static MainPacket create(int type) {
        MainPacket packet = new MainPacket();
        packet.type = (byte) type;
        return packet;
    }


    public byte[] toByteArray() {
        try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
            writer.write(new byte[]{HEADER_1, HEADER_2, HEADER_3});
            writer.write(type);

            switch (type) {
                case Name:
                    NameHandler.toByteArray(field, writer);
                    break;
                case Coordinates:
                    CoordinatesHandler.toByteArray(field, writer);
                    break;
                case Shot:
                    ShotHandler.toByteArray(field, writer);
                    break;
                case Players:
                    PlayersHandler.toByteArray(PlayersCoordinates, writer);
                    break;
            }


            writer.write(new byte[]{FOOTER_1, FOOTER_2, FOOTER_3});
            return writer.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static MainPacket parse(byte[] data) {
        if (data[0] != HEADER_1 && data[1] != HEADER_2 && data[2] != HEADER_3 && data[data.length - 1] != FOOTER_3 &&
                data[data.length - 2] != FOOTER_2 && data[data.length - 3] != FOOTER_1) {
            throw new RuntimeException("Wrong packet!");
        }
        byte type = data[3];

        switch (type) {
            case Name:
                return NameHandler.parse(data);
            case Coordinates:
                return CoordinatesHandler.parse(data);
            case Shot:
                return ShotHandler.parse(data);
            case Exit:
                return ExitHandler.parse(data);
            case Players:
                return PlayersHandler.parse(data);
            case BusyName:
                return ExitHandler.parse(data);
        }
        return null;
    }


    public void setValue(Object value) {
        switch (type) {
            case Name:
                if (value.getClass().equals(String.class)) {
                    field=NameHandler.setValue(value);
                }
                break;
            //ВЫВЕСТИ ОШИБКУ???
            case Coordinates:
                if (value.getClass().equals(LinkedList.class) || value.getClass().equals(ArrayList.class)) {
                    boolean allIsInteger = true;
                    for (int i = 0; i < ((java.util.AbstractList<?>) value).size(); i++) {
                        if (((AbstractList<?>) value).get(i).equals(Integer.class)) {
                            allIsInteger = false;
                        }
                    }
                    if (allIsInteger) {
                        field=CoordinatesHandler.setValue((java.util.AbstractList<Integer>) value);
                    } else {
                        throw new RuntimeException("Введеный список состоит не из чисел");
                    }
                } else {
                    throw new RuntimeException("Получен не список");
                }
                break;
            case Shot:
                if (value.getClass().equals(LinkedList.class) || value.getClass().equals(ArrayList.class)) {
                    boolean allIsInteger = true;
                    for (int i = 0; i < ((java.util.AbstractList<?>) value).size(); i++) {
                        if (((AbstractList<?>) value).get(i).equals(Integer.class)) {
                            allIsInteger = false;
                        }
                    }
                    if (allIsInteger) {
                        field=ShotHandler.setValue((java.util.AbstractList<Integer>) value);
                    } else {
                        throw new RuntimeException("Введеный список состоит не из чисел");
                    }
                } else {
                    throw new RuntimeException("Получен не список");
                }
                break;

            case Players:
                if (value.getClass().equals(LinkedList.class) || value.getClass().equals(ArrayList.class)) {
                    boolean allIsInteger = true;
                    if (allIsInteger) {
                        PlayersCoordinates=PlayersHandler.setValue((java.util.AbstractList<AbstractList<Object>>) value);
                    } else {
                        throw new RuntimeException("Введеный список состоит не из чисел");
                    }
                } else {
                    throw new RuntimeException("Получен не список");
                }
                break;
        }
    }



    public <T> T getValue(Class<T> clazz) {
        switch (type) {
            case Name:
                return (T) NameHandler.getValue(field, String.class);
            case Coordinates:
                return (T) CoordinatesHandler.getValue(field, List.class);
            case Shot:
                return (T) ShotHandler.getValue(field, List.class);
            case Players:
                return (T) PlayersHandler.getValue(PlayersCoordinates, List.class);

        }
        return null;
    }

}
