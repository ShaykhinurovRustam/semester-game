package client;

import protocol.MainPacket;
import server.Main;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;
import sprite.Sprite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


//@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client extends Thread{

    Socket clientSocket;

    int x=-1;
    int y=-1;

    public InputStream inputStream;
    public OutputStream outputStream;

    List<List<Object>> players;

    String name;

    public boolean gameIsFinished=false;
    public boolean leaveApp=false;

    List<Sprite> otherPlayerAndBullets=new LinkedList<>();


    public boolean isGameIsFinished(){
        return gameIsFinished;
    }

    public OutputStream getOutputStream(){
        return outputStream;
    }

    public List<Sprite> getOtherPlayerAndBullets(){
        return otherPlayerAndBullets;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }


    public static Client create(String host, Integer port) {
        try {
            Client client = new Client();
            client.clientSocket = new Socket(host, port);
            client.inputStream=client.clientSocket.getInputStream();
            client.outputStream=client.clientSocket.getOutputStream();
            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] extendArray(byte[] oldArray) {
        int oldSize = oldArray.length;
        byte[] newArray = new byte[oldSize * 2];
        System.arraycopy(oldArray, 0, newArray, 0, oldSize);
        return newArray;
    }

    byte[] readInput(InputStream stream) throws IOException {
        int b;
        byte[] buffer = new byte[10];
        int counter = 0;
        while ((b = stream.read()) > -1) {
            buffer[counter++] = (byte) b;
            if (counter >= buffer.length) {
                buffer = extendArray(buffer);
            }
            if (counter > 2 && MainPacket.compareEOP(buffer, counter - 1)) {
                break;
            }
        }
        byte[] data = new byte[counter];
        System.arraycopy(buffer, 0, data, 0, counter);
        return data;
    }


    public void run(){
        try {
            x=-1;
            y=-1;
            int type=5;
            MainPacket startCoordinate = null;
            while (type==5 || type==4){
                byte[] StartCoordinateData=readInput(inputStream);
                startCoordinate= MainPacket.parse(StartCoordinateData);
                type=(int)startCoordinate.getType();
            }
            LinkedList<Integer> startXandY=startCoordinate.getValue(LinkedList.class);
            x=startXandY.get(0);
            y=startXandY.get(1);
            while(true){
                byte[] ServerData=readInput(inputStream);
                MainPacket packet= MainPacket.parse(ServerData);
                if(packet.getType()==4 || gameIsFinished){
                    x=0;
                    gameIsFinished=true;
                    MainPacket packetFromClientToServer = MainPacket.create(4);
                    try {
                        outputStream.write(packetFromClientToServer.toByteArray());
                        outputStream.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    this.run();
                    break;
                }
                players=packet.getValue(List.class);
                List<Sprite> otherObjects=new LinkedList<>();
                for(List<Object> player: players){
                    Double XCoordinate= (Double) player.get(0);
                    Integer XCoordinateInt=XCoordinate.intValue();
                    Double YCoordinate= (Double) player.get(1);
                    Integer YCoordinateInt=YCoordinate.intValue();
                    Main.Direction direction=null;
                    switch ((byte)player.get(3)){
                        case 1:
                            direction= Main.Direction.UP;
                            break;
                        case 2:
                            direction= Main.Direction.RIGHT;
                            break;
                        case 3:
                            direction= Main.Direction.DOWN;
                            break;
                        case 4:
                            direction= Main.Direction.LEFT;
                            break;
                    }
                    if(player.get(2)==null){
                        otherObjects.add(new Sprite( XCoordinateInt,YCoordinateInt,3,3,"bullet", Color.WHITE,null,null,null,(String)player.get(2)));
                    }else{
                        otherObjects.add(new Sprite( XCoordinateInt,YCoordinateInt,30,30,"otherPlayer", Color.RED,direction,null,null,(String)player.get(2)));
                    }
                }
                otherPlayerAndBullets=otherObjects;


            }
        } catch (IOException e) {
            System.out.println("Игра завершена!");
        }
    }

}