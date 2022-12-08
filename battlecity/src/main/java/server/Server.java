package server;

import protocol.MainPacket;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javafx.scene.paint.Color;
import server.Direction;
import sprite.Sprite;

@NoArgsConstructor
public class Server extends Thread {

    List<Sprite> allObjects = new LinkedList<>();
    List<String> allNickNames=new LinkedList<>();

    ServerSocket serverSocket;
    Socket clientSocket;


    public List<Sprite> getAllObjects() {
        return allObjects;
    }

    public void setAllObjects(List<Sprite> allObjects) {
        this.allObjects = allObjects;
    }


    public class Tank extends Thread {
        int x;
        int y;
        String name;
        InputStream inputStream;
        OutputStream outputStream;
        TankInputThread inputThread;

        public Tank(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        @NoArgsConstructor
        private class TankInputThread extends Thread {
            public boolean leaveGame = false;
            Tank player;

            public TankInputThread(Tank player) {
                this.player = player;
            }

            public void run() {
                try {
                    while (true) {
                        byte[] dataFromUser = readInput(inputStream);
                        MainPacket packetFromUser = MainPacket.parse(dataFromUser);
                        switch (packetFromUser.getType()) {
                            case 2:
                                List<Integer> XY = packetFromUser.getValue(List.class);
                                for (Sprite sprite : allObjects) {
                                    if (sprite.getNickName() != null && sprite.getNickName().equals(name)) {
                                        if (sprite.getTranslateX() - XY.get(0) > 0) {
                                            sprite.setDirection(Direction.LEFT);
                                        }
                                        if (sprite.getTranslateX() - XY.get(0) < 0) {
                                            sprite.setDirection(Direction.RIGHT);
                                        }
                                        if (sprite.getTranslateY() - XY.get(1) > 0) {
                                            sprite.setDirection(Direction.UP);
                                        }
                                        if (sprite.getTranslateY() - XY.get(1) < 0) {
                                            sprite.setDirection(Direction.DOWN);
                                        }
                                        sprite.setTranslateX(XY.get(0));
                                        sprite.setTranslateY(XY.get(1));
                                    }
                                }
                                break;
                            case 3:
                                List<Integer> XYShot = packetFromUser.getValue(List.class);
                                x = XYShot.get(0);
                                y = XYShot.get(1);
                                Direction direction = null;
                                switch (XYShot.get(2)) {
                                    case 1:
                                        direction = Direction.UP;
                                        break;
                                    case 2:
                                        direction = Direction.RIGHT;
                                        break;
                                    case 3:
                                        direction = Direction.DOWN;
                                        break;
                                    case 4:
                                        direction = Direction.LEFT;
                                        break;

                                }
                                allObjects.add(new Sprite(XYShot.get(0), XYShot.get(1), 3, 3, "bullet", Color.WHITE, direction, null, null, null));
                                break;
                            case 4:
                                leaveGame = true;
                                for (Sprite sprite : allObjects) {
                                    if (sprite.getNickName() != null && sprite.getNickName().equals(name)) {
                                        sprite.dead = leaveGame;
                                    }
                                }
                                break;
                        }


                        if (leaveGame) {
                            player.run();
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }


        public void run() {
            try {
                byte[] dataFromUser = readInput(inputStream);
                if(dataFromUser.length==0){
                    for(int i=0;i<allNickNames.size();i++){
                        if(allNickNames.get(i).equals(name)){
                            allNickNames.remove(i);
                            break;
                        }
                    }
                    inputStream.close();
                    outputStream.close();
                    return;
                }
                MainPacket packetFromUser = MainPacket.parse(dataFromUser);
                if (packetFromUser.getType() == 1) {
                    if(name==null){
                        name = packetFromUser.getValue(String.class);
                        System.out.println(allNickNames.toString());
                        for(String nick: allNickNames){
                            if(nick.equals(name)){
                                name=null;
                                LinkedList<Integer> XAndY = new LinkedList<>();
                                XAndY.add(-2);
                                XAndY.add(-2);
                                MainPacket startCoordinate=MainPacket.create(2);
                                startCoordinate.setValue(XAndY);
                                outputStream.write(startCoordinate.toByteArray());
                                outputStream.flush();
                                outputStream.write(MainPacket.create(4).toByteArray());
                                outputStream.flush();
                                dataFromUser = readInput(inputStream);
                                packetFromUser = MainPacket.parse(dataFromUser);
                                run();
                                return;
                            }
                        }
                        allNickNames.add(name);
                    }
                    System.out.println(allNickNames.toString());
                    while (true) {
                        System.out.println("Определяем позицию");
                        boolean occupied = false;
                        for (Sprite sprite : allObjects) {
                            if (sprite.getTranslateX() < 90 && sprite.getTranslateY() < 90) {
                                occupied = true;
                            }
                        }
                        if (!occupied) {
                            MainPacket startCoordinate = MainPacket.create(2);
                            LinkedList<Integer> XAndY = new LinkedList<>();
                            XAndY.add(0);
                            XAndY.add(0);
                            startCoordinate.setValue(XAndY);
                            outputStream.write(startCoordinate.toByteArray());
                            outputStream.flush();
                            allObjects.add(new Sprite(0, 0, 30, 30, "player", Color.BLUE, Direction.UP, inputStream, outputStream, name));
                            break;
                        }
                        occupied = false;
                        for (Sprite sprite : allObjects) {
                            if (sprite.getTranslateX() > 300 && sprite.getTranslateY() < 90) {
                                occupied = true;
                            }
                        }
                        if (!occupied) {
                            MainPacket startCoordinate = MainPacket.create(2);
                            LinkedList<Integer> XAndY = new LinkedList<>();
                            XAndY.add(360);
                            XAndY.add(0);
                            startCoordinate.setValue(XAndY);
                            outputStream.write(startCoordinate.toByteArray());
                            outputStream.flush();
                            allObjects.add(new Sprite(360, 0, 30, 30, "player", Color.BLUE, Direction.UP, inputStream, outputStream, name));
                            break;
                        }
                        occupied = false;
                        for (Sprite sprite : allObjects) {
                            if (sprite.getTranslateX() < 90 && sprite.getTranslateY() > 300) {
                                occupied = true;
                            }
                        }
                        if (!occupied) {
                            MainPacket startCoordinate = MainPacket.create(2);
                            LinkedList<Integer> XAndY = new LinkedList<>();
                            XAndY.add(0);
                            XAndY.add(360);
                            startCoordinate.setValue(XAndY);
                            outputStream.write(startCoordinate.toByteArray());
                            outputStream.flush();
                            allObjects.add(new Sprite(0, 360, 30, 30, "player", Color.BLUE, Direction.UP, inputStream, outputStream, name));
                            break;
                        }
                        occupied = false;
                        for (Sprite sprite : allObjects) {
                            if (sprite.getTranslateX() > 300 && sprite.getTranslateY() > 300) {
                                occupied = true;
                            }
                        }
                        if (!occupied) {
                            MainPacket startCoordinate = MainPacket.create(2);
                            LinkedList<Integer> XAndY = new LinkedList<>();
                            XAndY.add(360);
                            XAndY.add(360);
                            startCoordinate.setValue(XAndY);
                            outputStream.write(startCoordinate.toByteArray());
                            outputStream.flush();
                            allObjects.add(new Sprite(360, 360, 30, 30, "player", Color.BLUE, Direction.UP, inputStream, outputStream, name));
                            break;
                        }
                    }
                } else {
                    System.out.println(packetFromUser.getType());
                    this.run();
                }


                inputThread = new TankInputThread(this);
                inputThread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void initConnection() {
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                Tank player = new Tank(clientSocket.getInputStream(), clientSocket.getOutputStream());
                player.start();
            }
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

    public static Server create(Integer port) {
        try {
            Server server = new Server();
            server.serverSocket = new ServerSocket(port);
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
