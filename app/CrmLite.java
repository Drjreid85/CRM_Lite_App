package app;

import crm.db.Db;
import crm.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CrmLite extends Application {

  @Override
  public void start(Stage stage) {
    // Ensure DB exists & schema is applied
    Db.init();

    Scene scene = new Scene(new MainView().getRoot(), 980, 640);
    stage.setTitle("CRM Lite");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
