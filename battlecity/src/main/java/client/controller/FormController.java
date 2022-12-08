package client.controller;


import protocol.MainPacket;
import client.Main;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Data;
import sprite.Sprite;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;


@Data
public class FormController implements Initializable {

    OutputStream outputStream = Main.client.getOutputStream();

    MainPacket packetFromClientToServer;

    List<sprite.Sprite> allObjects;
    public Direction direction = null;
    private Sprite player;
    private Pane root = new Pane();
    private double t = 0;
    boolean firstLaunch=true;
    @FXML
    TextField nameTextField;
    AnimationTimer timer;

    private Stage stage;
    private Scene scene;

    private Parent createContent() {
        root.setPrefSize(390, 390);

        root.getChildren().add(player);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        timer.start();


        return root;
    }


    private void update() {
        t += 0.016;

        if(Main.client.isGameIsFinished()){
            try {
                Main.client.gameIsFinished=false;
                timer.stop();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/formForRespawn.fxml")));
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                scene.setOnKeyPressed(e->{
                    switch (e.getCode()){
                        case ESCAPE:
                            Main.client.leaveApp=true;
                            try {
                                Main.client.inputStream.close();
                                Main.client.outputStream.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            stage.close();
                    }
                });
                return;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        allObjects=Main.client.getOtherPlayerAndBullets();


//        System.out.println(allObjects.toString());
        System.out.println(firstLaunch);

        List<Sprite> listOfSpritesFromRoot=new LinkedList<>();
        List<Sprite> listOfLevelBlocks=new LinkedList<>();

        for(Node node:root.getChildren()){
            if(!node.getClass().equals(Text.class)){
                listOfSpritesFromRoot.add((Sprite) node);
            }
        }

        for(Sprite sprite: listOfSpritesFromRoot){
            if(sprite.type.contains("level")){
                listOfLevelBlocks.add(sprite);
            }
        }

        root.getChildren().clear();

        root.getChildren().add(player);

        for(Node sprite: listOfLevelBlocks){
            root.getChildren().add(sprite);
        }

        for(Sprite sprite: allObjects){
            if(sprite.getNickName()!=null){
                if(!sprite.getNickName().equals(player.getNickName())){
                    switch (sprite.getDirection()){
                        case UP:
                            Image img = new Image("/images/EnemyUP.png");
                            sprite.setFill(new ImagePattern(img));
                            break;
                        case RIGHT:
                            img = new Image("/images/EnemyRIGHT.png");
                            sprite.setFill(new ImagePattern(img));
                            break;
                        case DOWN:
                            img = new Image("/images/EnemyDOWN.png");
                            sprite.setFill(new ImagePattern(img));
                            break;
                        case LEFT:
                            img = new Image("/images/EnemyLEFT.png");
                            sprite.setFill(new ImagePattern(img));
                            break;
                    }
                    root.getChildren().add(sprite);
                    Text nick=new Text(sprite.getNickName());
                    nick.setFill(Color.RED);
                    nick.setTranslateX(sprite.getTranslateX());
                    nick.setTranslateY(sprite.getTranslateY()+35);
                    root.getChildren().add(nick);
                }
            }else{ // ПУСТОЕ ИМЯ ВОЗМОЖНО ТОЛЬКО У ПУЛЬ
                root.getChildren().add(sprite);
            }
        }

        if (t > 2) {
            t = 0;
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }


    public void login(ActionEvent event) throws IOException {



        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();




        //ОТПРАВЛЯЕМ СЕРВЕРУ НАШ НИК
        packetFromClientToServer = MainPacket.create(1);
        if(nameTextField!=null){
            TextField userName = new TextField(nameTextField.getText());
            packetFromClientToServer.setValue(userName.getText());
        }else{
            packetFromClientToServer.setValue(player.getNickName());
        }

        try {
            outputStream.write(packetFromClientToServer.toByteArray());
            outputStream.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        while (true){
            System.out.println();
            if(Main.client.getX()!=-1){
                break;
            }
        }

        player = new Sprite(Main.client.getX(), Main.client.getY(), 30, 30, "player", Color.BLUE, null,null,null,null);
        if(nameTextField!=null){
            player.setNickName(nameTextField.getText());
            firstLaunch=false;
        }


        Scene scene = new Scene(createContent(), 390, 390, Color.BLACK);
        List<Rectangle> rectangles = new LinkedList<>();
        rectangles.add(new Sprite(30, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 30, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(30, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 60, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(30, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 90, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(180, 90, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(30, 120, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 120, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 120, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 120, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(150, 150, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 150, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(0, 180, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(60, 180, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 180, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 180, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(300, 180, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(360, 180, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(150, 210, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(180, 210, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 210, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(150, 240, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 240, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(30, 270, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 270, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 270, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 270, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 270, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 270, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(30, 300, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 300, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 300, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 300, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(30, 330, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(90, 330, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 330, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(210, 330, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(270, 330, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(330, 330, 30, 30, "level1", Color.BLUE));

        rectangles.add(new Sprite(210, 360, 30, 30, "level1", Color.BLUE));
        rectangles.add(new Sprite(150, 360, 30, 30, "level1", Color.BLUE));




        Image img = new Image("/images/block.png");
        for (Rectangle rectangle : rectangles) {
            rectangle.setFill(new ImagePattern(img));
            root.getChildren().add(rectangle);
        }

        img = new Image("/images/UP.png");
        player.setFill(new ImagePattern(img));



        root.setStyle("-fx-background-color: black");
        AtomicBoolean collision = new AtomicBoolean(false);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A:
                    direction = Direction.LEFT;
                    Image dynamicImage = new Image("/images/LEFT.png");
                    player.setFill(new ImagePattern(dynamicImage));
                    player.moveLeft();
                    for (Rectangle rectangle : rectangles) {
                        if (player.getTranslateX() < rectangle.getTranslateX() + rectangle.getWidth() &&
                                player.getTranslateX() + player.getWidth() > rectangle.getTranslateX() &&
                                player.getTranslateY() < rectangle.getTranslateY() + rectangle.getHeight() &&
                                player.getTranslateY() + player.getHeight() > rectangle.getTranslateY()) {
                            collision.set(true);
                        }
                    }
                    for(Sprite sprite: allObjects){
                        if( sprite.getNickName()!=null &&!sprite.getNickName().equals(player.getNickName()) && sprite.type.equals("otherPlayer")){
                            if(sprite.getBoundsInParent().intersects(player.getBoundsInParent())){
                                collision.set(true);
                            }
                        }
                    }

                    if (collision.get()) {
                        player.moveRight();
                    }
                    packetFromClientToServer = MainPacket.create(2);
                    List<Integer> XAndY = new LinkedList<>();
                    XAndY.add((int) player.getTranslateX());
                    XAndY.add((int) player.getTranslateY());
                    packetFromClientToServer.setValue(XAndY);
                    try {
                        outputStream.write(packetFromClientToServer.toByteArray());
                        outputStream.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                    collision.set(false);
                    break;
                case D:
                    direction = Direction.RIGHT;
                    dynamicImage = new Image("/images/RIGHT.png");
                    player.setFill(new ImagePattern(dynamicImage));
                    player.moveRight();
                    for (Rectangle rectangle : rectangles) {
                        if (player.getTranslateX() + player.getWidth() > rectangle.getTranslateX() &&
                                player.getTranslateX() < rectangle.getTranslateX() + rectangle.getWidth() &&
                                (player.getTranslateY() < rectangle.getTranslateY() + rectangle.getHeight() &&
                                        player.getTranslateY() + player.getHeight() > rectangle.getTranslateY())) {
                            collision.set(true);
                        }
                    }
                    for(Sprite sprite: allObjects){
                        if(sprite.getNickName()!=null &&!sprite.getNickName().equals(player.getNickName())){
                            if(sprite.getBoundsInParent().intersects(player.getBoundsInParent()) && sprite.type.equals("otherPlayer")){
                                collision.set(true);
                            }
                        }
                    }
                    if (collision.get()) {
                        player.moveLeft();
                    }
                    packetFromClientToServer = MainPacket.create(2);
                    XAndY = new LinkedList<>();
                    XAndY.add((int) player.getTranslateX());
                    XAndY.add((int) player.getTranslateY());
                    packetFromClientToServer.setValue(XAndY);
                    try {
                        outputStream.write(packetFromClientToServer.toByteArray());
                        outputStream.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    collision.set(false);
                    break;
                case W:
                    direction = Direction.UP;
                    dynamicImage = new Image("/images/UP.png");
                    player.setFill(new ImagePattern(dynamicImage));
                    player.moveUp();
                    for (Rectangle rectangle : rectangles) {
                        if (player.getTranslateY() < rectangle.getTranslateY() + rectangle.getHeight() &&
                                player.getTranslateY() + player.getHeight() > rectangle.getTranslateY() &&
                                player.getTranslateX() < rectangle.getTranslateX() + rectangle.getWidth() &&
                                player.getTranslateX() + player.getWidth() > rectangle.getTranslateX()) {
                            collision.set(true);
                        }
                    }

                    for(Sprite sprite: allObjects){
                        if( sprite.getNickName()!=null &&!sprite.getNickName().equals(player.getNickName())){
                            if(sprite.getBoundsInParent().intersects(player.getBoundsInParent()) && sprite.type.equals("otherPlayer")){
                                collision.set(true);
                            }
                        }
                    }
                    if (collision.get()) {
                        player.moveDown();
                    }
                    packetFromClientToServer = MainPacket.create(2);
                    XAndY = new LinkedList<>();
                    XAndY.add((int) player.getTranslateX());
                    XAndY.add((int) player.getTranslateY());
                    packetFromClientToServer.setValue(XAndY);
                    try {
                        outputStream.write(packetFromClientToServer.toByteArray());
                        outputStream.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    collision.set(false);
                    break;
                case S:
                    direction = Direction.DOWN;
                    dynamicImage = new Image("/images/DOWN.png");
                    player.setFill(new ImagePattern(dynamicImage));
                    player.moveDown();
                    for (Rectangle rectangle : rectangles) {
                        if (player.getTranslateY() + player.getHeight() > rectangle.getTranslateY() &&
                                player.getTranslateY() < rectangle.getTranslateY() + rectangle.getHeight() &&
                                (player.getTranslateX() < rectangle.getTranslateX() + rectangle.getWidth() &&
                                        player.getTranslateX() + player.getWidth() > rectangle.getTranslateX())) {
                            collision.set(true);
                        }
                    }
                    for(Sprite sprite: allObjects){
                        if(sprite.getNickName()!=null && !sprite.getNickName().equals(player.getNickName())){
                            if(sprite.getBoundsInParent().intersects(player.getBoundsInParent()) && sprite.type.equals("otherPlayer")){
                                collision.set(true);
                            }
                        }
                    }
                    if (collision.get()) {
                        player.moveUp();
                    }
                    packetFromClientToServer = MainPacket.create(2);
                    XAndY = new LinkedList<>();
                    XAndY.add((int) player.getTranslateX());
                    XAndY.add((int) player.getTranslateY());
                    packetFromClientToServer.setValue(XAndY);
                    try {
                        outputStream.write(packetFromClientToServer.toByteArray());
                        outputStream.flush();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    collision.set(false);
                    break;
                case SPACE:
                    switch (direction) {
                        case UP:
                            packetFromClientToServer = MainPacket.create(3);
                            XAndY = new LinkedList<>();
                            XAndY.add((int) ((int) player.getTranslateX()+(player.getWidth()/2)));
                            XAndY.add((int) player.getTranslateY());
                            XAndY.add(1);
                            packetFromClientToServer.setValue(XAndY);
                            try {
                                outputStream.write(packetFromClientToServer.toByteArray());
                                outputStream.flush();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                        case DOWN:

                            packetFromClientToServer = MainPacket.create(3);
                            XAndY = new LinkedList<>();
                            XAndY.add((int) ((int) player.getTranslateX() + (player.getWidth() / 2)));
                            XAndY.add((int) ((int) player.getTranslateY() + (player.getHeight() / 2))+15);
                            XAndY.add(3);
                            packetFromClientToServer.setValue(XAndY);
                            try {
                                outputStream.write(packetFromClientToServer.toByteArray());
                                outputStream.flush();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                        case LEFT:

                            packetFromClientToServer = MainPacket.create(3);
                            XAndY = new LinkedList<>();
                            XAndY.add((int) player.getTranslateX());
                            XAndY.add((int) ((int) player.getTranslateY() + (player.getHeight() / 2)));
                            XAndY.add(4);
                            packetFromClientToServer.setValue(XAndY);
                            try {
                                outputStream.write(packetFromClientToServer.toByteArray());
                                outputStream.flush();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                        case RIGHT:

                            packetFromClientToServer = MainPacket.create(3);
                            XAndY = new LinkedList<>();
                            XAndY.add((int) ((int) player.getTranslateX() + player.getWidth()));
                            XAndY.add((int) ((int) player.getTranslateY() + (player.getHeight() / 2)));
                            XAndY.add(2);
                            packetFromClientToServer.setValue(XAndY);
                            try {
                                outputStream.write(packetFromClientToServer.toByteArray());
                                outputStream.flush();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;
                    }
                    break;
                case ESCAPE:
                    Main.client.gameIsFinished=true;
                    stage.close();
            }
        });
        stage.setScene(scene);
        stage.setTitle("TANKS!");
        stage.show();
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
}
