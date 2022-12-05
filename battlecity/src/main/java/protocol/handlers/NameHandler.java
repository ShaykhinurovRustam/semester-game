package protocol.handlers;

import protocol.MainPacket;
import protocol.fields.Field;
import protocol.fields.NameField;

import java.io.*;
import java.util.List;

public class NameHandler {



    public static void toByteArray(Field field, ByteArrayOutputStream writer) {
        try {
            writer.write(field.getId());
            Field field1 = new NameField((byte)field.getId(), field.getSize().get(0),field.getContent().get(0));
            List<Byte> sizes = field1.getSize();
            for (Byte size : sizes) {
                writer.write(size);
            }
            /**
             * ВОЗНИКАЕТ ВОПРОС, ЗАЧЕМ МЕТОД ВОЗВРАЩАЕТ СПИСОК
             * ОТВЕТ
             * МЫ СДЕЛАЛИ НАСЛЕДОВАНИЕ КЛАССОВ FIELD ОТ ОДНОГО ЕДИНСТЕНННОГО, ПОЛЕ КОТОРГО
             * ХРАНИТЬСЯ В ПАКЕТЕ, ЧТОБЫ МОЖНО БЫЛО БЫ СДЕЛАТЬ ВОСХОДЯЩЕЕ ПРЕОБРАЗОВАНИЕ, МЕТОДЫ
             * ДОЛЖНЫ ОСТАТЬЯ ТЕ ЖЕ. ВОЗВРАЩАТЬ СПИСОК НАМ ПОНАДОБИТЬСЯ В ВЫСТРЕЛЕ И КООРДИНАТАХ
             * ТАМ ПОЛЕ БУДЕТ ХРАНИТЬ В СЕБЕ НЕ ОДИН МАССИВ ИНФОРМАЦИИ А 3 И 2 СООТВЕТСВЕННО
             */
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
        byte fieldId =data[4];
        byte fieldSize =data[5];

        byte[] content = new byte[Byte.toUnsignedInt(fieldSize)];
        if (fieldSize != 0) {
            System.arraycopy(data, 6, content, 0, Byte.toUnsignedInt(fieldSize));
        }

        NameField field=new NameField(fieldId,fieldSize,content);
        packet.field=field;

        return packet;
    }

    public static Field setValue(Object value) {
        Field field;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(value);
            byte[] data = bos.toByteArray();
            if (data.length > 255) {
                throw new IllegalArgumentException("Too much data sent");
            }
            field=new NameField((byte)1,(byte)data.length,data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    public static <T> T getValue(Field field, Class<T> clazz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(field.getContent().get(0));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
