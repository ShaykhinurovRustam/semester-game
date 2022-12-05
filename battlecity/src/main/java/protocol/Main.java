package protocol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) {



        MainPacket packet=MainPacket.create(6);
        byte[] checkMassiv=packet.toByteArray();
        MainPacket newPacket=MainPacket.parse(checkMassiv);
        System.out.println(newPacket.getType());

//        MainPacket send=MainPacket.create(1);
//        send.setValue("daniils1406");
//        byte[] message=send.toByteArray();
//        MainPacket get= MainPacket.parse(message);
//        System.out.println(get.getValue(String.class));


//        MainPacket send=MainPacket.create(2);
//        List<Integer> XAndY=new LinkedList<>();
//        XAndY.add(13);
//        XAndY.add(17);
//        send.setValue(XAndY);
//        byte[] message=send.toByteArray();
//        MainPacket get= MainPacket.parse(message);
//        List<Integer> XY=get.getValue(List.class);
//        System.out.println(XY.toString());

//        MainPacket send=MainPacket.create(3);
//        List<Integer> XAndYAndShot=new LinkedList<>();
//        XAndYAndShot.add(13);
//        XAndYAndShot.add(17);
//        XAndYAndShot.add(1);
//        send.setValue(XAndYAndShot);
//        byte[] message=send.toByteArray();
//        MainPacket get= MainPacket.parse(message);
//        List<Integer> XY=get.getValue(List.class);
//        System.out.println(XY.toString());


//        MainPacket send=MainPacket.create(4);
//        byte[] message=send.toByteArray();
//        MainPacket get=MainPacket.parse(message);
//        System.out.println(get.getType());


//        MainPacket send=MainPacket.create(5);
//        List<List<Object>> Players=new LinkedList<>();
//        List<Object> XAndY1=new LinkedList<>();
//        XAndY1.add(13);
//        XAndY1.add(17);
//        XAndY1.add("sacd");
//        XAndY1.add((byte)2);
//        XAndY1.add((byte)2);
//        List<Object> XAndY2=new LinkedList<>();
//        XAndY2.add(11);
//        XAndY2.add(12);
//        XAndY2.add("dcs");
//        List<Integer> XAndY3=new LinkedList<>();
//        XAndY3.add(1);
//        XAndY3.add(7);
//        Players.add(XAndY1);
//        Players.add(XAndY2);
//        Players.add(XAndY3);
//        send.setValue(Players);
//        byte[] message=send.toByteArray();
//        System.out.println(Arrays.toString(message));
//        MainPacket get= MainPacket.parse(message);

//        List<List<Object>> PlayersCoordinate=get.getValue(List.class);
//        for(List<Object> player: PlayersCoordinate){
//            for(Object ob:player){
//                System.out.println(ob);
//            }
//        }
//        System.out.println(PlayersCoordinate.toString());


    }
}
