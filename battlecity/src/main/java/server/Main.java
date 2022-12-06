package server;

import protocol.MainPacket;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import sprite.Sprite;


public class Main extends Application {

    private Pane root = new Pane();
    private double t = 0;
    PrintWriter printWriter;

    {
        try {
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream("src/main/java/Server/logs.txt")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    static Server server = Server.create(4444);


    public static void main(String[] args) {
        NewThread thread = new NewThread();
        thread.start();
        launch(args);
    }


    public static class NewThread extends Thread {
        public void run() {
            server.initConnection();
        }
    }


    private Parent createContent() {
        root.setPrefSize(390, 390);


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };

        timer.start();


        return root;
    }


    private List<Sprite> sprites(List<Sprite> list) {
        return list.stream().filter(s -> s.getClass().equals(Sprite.class)).map(n -> (Sprite) n).collect(Collectors.toList());
    }

    private void update() {
        t += 0.016;

        List<Sprite> allObjects = server.getAllObjects();

//        System.out.println(allObjects.toString());


        sprites(allObjects).forEach(s -> {
            switch (s.type) {
                case "bullet":
                    switch (s.direction) {
                        case UP:
                            s.moveUp();
                            break;
                        case DOWN:
                            s.moveDown();
                            break;
                        case LEFT:
                            s.moveLeft();
                            break;
                        case RIGHT:
                            s.moveRight();
                            break;
                    }
                    root.getChildren().stream().map(e -> (Sprite) e).filter(e -> e.type.contains("level")).forEach(levelBlock -> {
                        if (s.getBoundsInParent().intersects(levelBlock.getBoundsInParent())) {
//                            System.out.println("Пуля ударилассь об стену");
                            s.dead = true;
                        }
                    });

                    sprites(allObjects).stream().filter(e -> e.type.equals("player")).forEach(player -> {
                        if (s.getBoundsInParent().intersects(player.getBoundsInParent())) {
//                            System.out.println("Пуля ударилась об игрока");
                            s.dead = true;
                            player.dead = true;
                        }
                    });


                    if (s.getTranslateX() == 360 || s.getTranslateY() == 360 || s.getTranslateY() == 0 || s.getTranslateX() == 0) {
                        s.dead = true;
                    }
            }
        });

        List<List<Object>> players = new LinkedList<>();
        for (Sprite sprite : allObjects) {
            if (sprite.type.equals("player")) {
                if (sprite.dead) {
                    try {
                        sprite.outputStream.write(MainPacket.create(4).toByteArray());
                        sprite.outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }

            }
            List<Object> playerInfo = new LinkedList<>();
            playerInfo.add(sprite.getTranslateX());
            playerInfo.add(sprite.getTranslateY());
            playerInfo.add(sprite.getNickName());
            switch (sprite.getDirection()){
                case UP:
                    playerInfo.add((byte)1);
                    break;
                case DOWN:
                    playerInfo.add((byte)3);
                    break;
                case LEFT:
                    playerInfo.add((byte)4);
                    break;
                case RIGHT:
                    playerInfo.add((byte)2);
                    break;
            }
            players.add(playerInfo);

        }

        MainPacket packet = MainPacket.create(5);
        packet.setValue(players);
        LinkedList<String> streams=new LinkedList<>();
        for (Sprite sprite : allObjects) {
            if (sprite.type.equals("player")) {
                try {
                    streams.add(sprite.outputStream.toString());
                    sprite.outputStream.write(packet.toByteArray());
                    sprite.outputStream.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }

//        System.out.println(allObjects.toString());



        allObjects.removeIf(n -> {
            Sprite s = (Sprite) n;
            return s.dead;
        });


        List<Sprite> listOfSpritesFromRoot = new LinkedList<>();
        List<Sprite> listOfLevelBlocks = new LinkedList<>();

        for (Node node : root.getChildren()) {
            listOfSpritesFromRoot.add((Sprite) node);
        }

        for (Sprite sprite : listOfSpritesFromRoot) {
            if (sprite.type.contains("level")) {
                listOfLevelBlocks.add(sprite);
            }
        }


        root.getChildren().clear();
        for (Node sprite : listOfLevelBlocks) {
            root.getChildren().add(sprite);
        }

        for (Sprite sprite : allObjects) {
            root.getChildren().add(sprite);
        }

        if (t > 2) {
            t = 0;
        }
    }


    @Override
    public void start(Stage stage) throws Exception {


        Scene scene = new Scene(createContent(), 390, 390, Color.BLACK);
        List<Rectangle> rectangles = new LinkedList<>();
        rectangles.add(new Sprite(180, 90, 30, 30, "level1", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(150, 150, 30, 30, "level2", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(210, 150, 30, 30, "level3", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(270, 180, 60, 30, "level4", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(360, 180, 30, 30, "level5", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(180, 210, 30, 30, "level6", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(30, 30, 30, 120, "level7", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(90, 30, 30, 120, "level8", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(270, 30, 30, 120, "level9", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(330, 30, 30, 120, "level10", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(210, 30, 30, 90, "level11", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(150, 30, 30, 90, "level12", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(210, 210, 30, 90, "level13", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(150, 210, 30, 90, "level14", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(210, 330, 30, 60, "level15", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(330, 270, 30, 90, "level16", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(270, 270, 30, 90, "level17", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(30, 270, 30, 90, "level18", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(90, 270, 30, 90, "level19", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(150, 330, 30, 60, "level20", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(60, 180, 60, 30, "level21", Color.BLUE, null, null, null, null));
        rectangles.add(new Sprite(0, 180, 30, 30, "level22", Color.BLUE, null, null, null, null));


        Image img = new Image("/images/block.png");
        for (Rectangle rectangle : rectangles) {
            rectangle.setFill(new ImagePattern(img));
            root.getChildren().add(rectangle);
        }



        root.setStyle("-fx-background-color: black");


        stage.setScene(scene);
        stage.setTitle("Hello FXml!");
        stage.show();
    }

}
