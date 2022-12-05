package sprite;

import server.Main;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Data;

import java.io.InputStream;
import java.io.OutputStream;

//@Data
public class Sprite extends Rectangle {

    public static int globalId=0;

    public int id;
    public boolean dead = false;
    public final String type;
    public Main.Direction direction = null;
    public InputStream inputStream;
    public OutputStream outputStream;
    String nickName;


    public Main.Direction getDirection(){
        return direction;
    }

    public void setDirection(Main.Direction direction){
        this.direction=direction;
    }

    public Sprite(int x, int y, int w, int h, String type, Color color, Main.Direction direction,InputStream inputStream,OutputStream outputStream,String nickName) {
        super(w, h, color);
        this.id=globalId++;
        this.inputStream=inputStream;
        this.outputStream=outputStream;
        this.nickName=nickName;
        if (direction != null) {
            switch (direction) {
                case LEFT:
                    this.direction = Main.Direction.LEFT;
                    break;
                case RIGHT:
                    this.direction = Main.Direction.RIGHT;
                    break;
                case DOWN:
                    this.direction = Main.Direction.DOWN;
                    break;
                case UP:
                    this.direction = Main.Direction.UP;
                    break;
            }
        }




        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
    }

    public Sprite(int x, int y, int w, int h, String type, Color color, Main.Direction direction) {
        super(w, h, color);
        if (direction != null) {
            switch (direction) {
                case LEFT:
                    this.direction = Main.Direction.LEFT;
                    break;
                case RIGHT:
                    this.direction = Main.Direction.RIGHT;
                    break;
                case DOWN:
                    this.direction = Main.Direction.DOWN;
                    break;
                case UP:
                    this.direction = Main.Direction.UP;
                    break;
            }
        }




        this.type = type;
        setTranslateX(x);
        setTranslateY(y);
    }
    public void moveLeft() {
        if (getTranslateX() - 5 > -4) {
            setTranslateX(getTranslateX() - 5);
        }
    }

    public void moveRight() {
        if (getTranslateX() + 5 < 364) {
            setTranslateX(getTranslateX() + 5);
        }
    }

    public void moveUp() {
        if (getTranslateY() - 5 > -4) {
            setTranslateY(getTranslateY() - 5);
        }
    }

    public void moveDown() {
        if (getTranslateY() + 5 < 364) {
            setTranslateY(getTranslateY() + 5);
        }
    }


    public String getNickName(){
        return nickName;
    }

    public void setNickName(String nickName){
        this.nickName=nickName;
    }

    @Override
    public String toString() {
        return "Sprite{" +
                "id=" + id +
                ", X=" + getTranslateX() +
                ", Y=" + getTranslateY() +
                ", dead=" + dead +
                ", type='" + type + '\'' +
                ", direction=" + direction +
                ", inputStream=" + inputStream +
                ", outputStream=" + outputStream +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}