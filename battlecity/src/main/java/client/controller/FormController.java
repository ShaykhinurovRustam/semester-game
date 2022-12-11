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


    List<Rectangle> rectangles = new LinkedList<>();

    MainPacket packetFromClientToServer;

    List<sprite.Sprite> allObjects;
    public Direction direction = null;
    private Sprite player;
    private String nickName;
    private Pane root = new Pane();
    private double t = 0;
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
            if(Main.client.nameIsBusy){
                try {
                    Main.client.gameIsFinished=false;
                    Main.client.nameIsBusy=false;
                    timer.stop();
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/form.fxml")));
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
        System.out.println(allObjects.toString());



//        List<Sprite> listOfSpritesFromRoot=new LinkedList<>();
//        List<Sprite> listOfLevelBlocks=new LinkedList<>();
//
//        for(Node node:root.getChildren()){
//            if(!node.getClass().equals(Text.class)){
//                listOfSpritesFromRoot.add((Sprite) node);
//            }
//        }
//
//        for(Sprite sprite: listOfSpritesFromRoot){
//            if(sprite.type.contains("level")){
//                listOfLevelBlocks.add(sprite);
//            }
//        }

        root.getChildren().clear();

        root.getChildren().add(player);

//        for(Node sprite: listOfLevelBlocks){
//            root.getChildren().add(sprite);
//        }

        for(Rectangle rectangle: rectangles){
            root.getChildren().add(rectangle);
        }

        for(Sprite sprite: allObjects){
            if(!sprite.getNickName().contains("bullet")){
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
                }else{
                    Main.client.kills= sprite.getKillingCount();
                    stage.setTitle("TANKS!"+""+Main.client.kills);
                }
            }else{
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
        rectangles=new LinkedList<>();



        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();




        //ОТПРАВЛЯЕМ СЕРВЕРУ НАШ НИК
        packetFromClientToServer = MainPacket.create(1);
        TextField userName = null;
        if(Main.client.getPlayerName()==null){
            userName = new TextField(nameTextField.getText());
            packetFromClientToServer.setValue(userName.getText());
        }else{
            packetFromClientToServer.setValue(Main.client.getPlayerName());
        }

        System.out.println(packetFromClientToServer.getValue(String.class));

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

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        player = new Sprite(Main.client.getX(), Main.client.getY(), 30, 30, "player", Color.BLUE, null,null,null,null);
        if(Main.client.getPlayerName()==null){
            player.setNickName(nameTextField.getText());
            Main.client.setPlayerName(userName.getText());
        }else{
            player.setNickName(Main.client.getPlayerName());
        }
        if(Main.client.nameIsBusy){
            Main.client.setPlayerName(null);
        }


        Scene scene = new Scene(createContent(), 390, 390, Color.BLACK);
        rectangles.add(new Rectangle(30, 30, 30, 30));
        rectangles.add(new Rectangle(90, 30, 30, 30));
        rectangles.add(new Rectangle(150, 30, 30, 30));
        rectangles.add(new Rectangle(210, 30, 30, 30));
        rectangles.add(new Rectangle(270, 30, 30, 30));
        rectangles.add(new Rectangle(330, 30, 30, 30));
        rectangles.add(new Rectangle(30, 60, 30, 30));
        rectangles.add(new Rectangle(90, 60, 30, 30));
        rectangles.add(new Rectangle(150, 60, 30, 30));
        rectangles.add(new Rectangle(210, 60, 30, 30));
        rectangles.add(new Rectangle(270, 60, 30, 30));
        rectangles.add(new Rectangle(330, 60, 30, 30));
        rectangles.add(new Rectangle(30, 90, 30, 30));
        rectangles.add(new Rectangle(90, 90, 30, 30));
        rectangles.add(new Rectangle(150, 90, 30, 30));
        rectangles.add(new Rectangle(210, 90, 30, 30));
        rectangles.add(new Rectangle(270, 90, 30, 30));
        rectangles.add(new Rectangle(330, 90, 30, 30));
        rectangles.add(new Rectangle(180, 90, 30, 30));

        rectangles.add(new Rectangle(30, 120, 30, 30));
        rectangles.add(new Rectangle(90, 120, 30, 30));
        rectangles.add(new Rectangle(270, 120, 30, 30));
        rectangles.add(new Rectangle(330, 120, 30, 30));

        rectangles.add(new Rectangle(150, 150, 30, 30));
        rectangles.add(new Rectangle(210, 150, 30, 30));

        rectangles.add(new Rectangle(0, 180, 30, 30));
        rectangles.add(new Rectangle(60, 180, 30, 30));
        rectangles.add(new Rectangle(90, 180, 30, 30));
        rectangles.add(new Rectangle(270, 180, 30, 30));
        rectangles.add(new Rectangle(300, 180, 30, 30));
        rectangles.add(new Rectangle(360, 180, 30, 30));

        rectangles.add(new Rectangle(150, 210, 30, 30));
        rectangles.add(new Rectangle(180, 210, 30, 30));
        rectangles.add(new Rectangle(210, 210, 30, 30));

        rectangles.add(new Rectangle(150, 240, 30, 30));
        rectangles.add(new Rectangle(210, 240, 30, 30));

        rectangles.add(new Rectangle(30, 270, 30, 30));
        rectangles.add(new Rectangle(90, 270, 30, 30));
        rectangles.add(new Rectangle(150, 270, 30, 30));
        rectangles.add(new Rectangle(210, 270, 30, 30));
        rectangles.add(new Rectangle(270, 270, 30, 30));
        rectangles.add(new Rectangle(330, 270, 30, 30));

        rectangles.add(new Rectangle(30, 300, 30, 30));
        rectangles.add(new Rectangle(90, 300, 30, 30));
        rectangles.add(new Rectangle(270, 300, 30, 30));
        rectangles.add(new Rectangle(330, 300, 30, 30));

        rectangles.add(new Rectangle(30, 330, 30, 30));
        rectangles.add(new Rectangle(90, 330, 30, 30));
        rectangles.add(new Rectangle(150, 330, 30, 30));
        rectangles.add(new Rectangle(210, 330, 30, 30));
        rectangles.add(new Rectangle(270, 330, 30, 30));
        rectangles.add(new Rectangle(330, 330, 30, 30));

        rectangles.add(new Rectangle(210, 360, 30, 30));
        rectangles.add(new Rectangle(150, 360, 30, 30));




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
                        if (player.getTranslateX() < rectangle.getX() + rectangle.getWidth() &&
                                player.getTranslateX() + player.getWidth() > rectangle.getX() &&
                                player.getTranslateY() < rectangle.getY() + rectangle.getHeight() &&
                                player.getTranslateY() + player.getHeight() > rectangle.getY()) {
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
                        if (player.getTranslateX() + player.getWidth() > rectangle.getX() &&
                                player.getTranslateX() < rectangle.getX() + rectangle.getWidth() &&
                                (player.getTranslateY() < rectangle.getY() + rectangle.getHeight() &&
                                        player.getTranslateY() + player.getHeight() > rectangle.getY())) {
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
                        if (player.getTranslateY() < rectangle.getY() + rectangle.getHeight() &&
                                player.getTranslateY() + player.getHeight() > rectangle.getY() &&
                                player.getTranslateX() < rectangle.getX() + rectangle.getWidth() &&
                                player.getTranslateX() + player.getWidth() > rectangle.getX()) {
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
                        if (player.getTranslateY() + player.getHeight() > rectangle.getY() &&
                                player.getTranslateY() < rectangle.getY() + rectangle.getHeight() &&
                                (player.getTranslateX() < rectangle.getX() + rectangle.getWidth() &&
                                        player.getTranslateX() + player.getWidth() > rectangle.getX())) {
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

}
