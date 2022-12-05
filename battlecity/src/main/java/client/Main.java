package client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {
    public static Client client;


    public static void main(String[] args) {
        client=Client.create("localhost",4444);
//        while(true){
//            client.start();
//            launch(args);
//        }
        client.start();
        launch(args);

    }




    @Override
    public void start(Stage stage) throws Exception {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/form.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
