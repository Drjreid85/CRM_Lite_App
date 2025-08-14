package utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Fx {
  private Fx() {}
  public static boolean confirm(String msg) {
    return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
      .showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
  }
}
