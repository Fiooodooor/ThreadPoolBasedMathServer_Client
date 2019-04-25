
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

import static java.lang.Integer.valueOf;

public class Controller {
    @FXML
    public Button connectButton;
    @FXML
    public Button disconnectButton;
    @FXML
    public Button sendRequestButton;
    @FXML
    public TextField hostInput, portInput;
    @FXML
    public TextField theEquation;
    @FXML
    public TextField imageHeight, imageWidth;
    @FXML
    public TextField imageStartX, imageEndX;
    @FXML
    public TextField imageStartY, imageEndY;
    @FXML
    public TextArea debugText;
    @FXML
    public ImageView theCanvas;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Pane equationPane;
    @FXML
    public MenuItem menuClose;

    private ClientSocketControler threadsController=null;
    @FXML
    public void initialize()
    {

    }

    @FXML
    protected void connectButtonPressed(ActionEvent event) {
        connectedButtonsSetter(true);
        if(threadsController==null) {
            threadsController = new ClientSocketControler(hostInput.getText(), valueOf(portInput.getText()), this);
        }
        else if(!threadsController.isConnected()) {
            threadsController.Connect(hostInput.getText(), valueOf(portInput.getText()));
        }
    }
    @FXML
    protected void disconnectButtonPressed(ActionEvent event) {
        connectedButtonsSetter(false);
        if(threadsController!=null) {
            threadsController.Disconnect();
        }
    }
    @FXML
    protected void sendButtonPressed(ActionEvent event) {
        if(threadsController != null) {
            sendButtonsSetter(true);
            try {
                JSONObject myData = new JSONObject();
                JSONObject myCanvas = new JSONObject();

                myCanvas.put("width", Integer.parseInt(this.imageWidth.getText()));
                myCanvas.put("height", Integer.parseInt(this.imageHeight.getText()));
                myCanvas.put("xStart",Double.parseDouble(this.imageStartX.getText()));
                myCanvas.put("xEnd", Double.parseDouble(this.imageEndX.getText()));
                myCanvas.put("yStart", Double.parseDouble(this.imageStartY.getText()));
                myCanvas.put("yEnd", Double.parseDouble(this.imageEndY.getText()));
                this.debugText.appendText(this.theEquation.getText());
                if (this.theEquation.getText().isEmpty()) {
                    throw (new NullPointerException());
                }
                myData.put("equation", this.theEquation.getText());
                myData.put("canvasSettings", myCanvas);
                threadsController.SendData("generate", myData);

            } catch(NullPointerException e) {
                debugText.appendText("Error: One of parameters is NULL! Correct it and try again!\n");
                sendButtonsSetter(false);
            } catch(NumberFormatException e) {
                debugText.appendText("Error: One of parameters has invalid value! Correct it and try again!\n");
                sendButtonsSetter(false);
            }
        }
        else {
            debugText.appendText("INFO: Thread controller not detected or no socket connection is working.\n");
        }
    }
    @FXML
    protected void applicationClose(ActionEvent event) {
        System.exit(0);
    }

    private void connectedButtonsSetter(boolean connected) {
        connectButton.setDisable(connected);
        disconnectButton.setDisable(!connected);
        sendButtonsSetter(!connected);
        progressBar.setProgress(0);
    }

    void sendButtonsSetter(boolean disable) {
        sendRequestButton.setDisable(disable);
        progressBar.setProgress(0);
    }
}
