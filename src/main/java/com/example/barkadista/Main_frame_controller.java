package com.example.barkadista;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Main_frame_controller implements Initializable {
    public AnchorPane anchorMain;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField adminEmail;
    @FXML
    private TextField adminPass;
    @FXML
    private AnchorPane admin_pane;
    @FXML
    private AnchorPane manual_pane;
    @FXML
    private TextField studentID;
    @FXML
    private AnchorPane student_pane;
    @FXML
    private Button adminBtn;
    @FXML
    private Button studentBtn;
    @FXML
    private Label sample;
    @FXML
    private ImageView shutdown_img;
    AdminDashboard adminDashboard;
    StudentRoomSelection studentRoomSelection;

    Double x_axis = 0.0;
    Double y_axis = 0.0;
    @FXML
    public void windowDrag(MouseEvent mouseEvent){
        Stage stage = (Stage) anchorMain.getScene().getWindow();
        stage.setY(mouseEvent.getScreenY() - y_axis);
        stage.setX(mouseEvent.getScreenX() - x_axis);
    };
    @FXML
    void windowPress(MouseEvent event){
        x_axis = event.getSceneX();
        y_axis = event.getSceneY();
    }
    public void shutdown(){
        Stage this_stage = (Stage)shutdown_img.getScene().getWindow();
        this_stage.close();
    }

    public void studentEnter() throws IOException, SQLException, ClassNotFoundException {
        Query query = new Query();
        String stu_id = studentID.getText();
        boolean proceed  = query.registerStudent(stu_id);

        if (proceed){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("student_room_selection.fxml"));
            Parent root = fxmlLoader.load();
            studentRoomSelection = fxmlLoader.getController();
            studentRoomSelection.setUser(stu_id);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
            Stage this_stage = (Stage) adminBtn.getScene().getWindow();
            this_stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Student ID");
            alert.setTitle("Student ID Error");
            alert.show();
        }

    }

    public void adminEnter() throws IOException, SQLException, ClassNotFoundException {
        Query query = new Query();
        ArrayList<ArrayList<String>>adminRows = query.readData("admin");
        String admin_em = adminEmail.getText();
        String admin_ps = adminPass.getText();
        String admin_id = "";
        boolean proceed = false;
        for (int i = 0; i < adminRows.size(); i ++){
            ArrayList <String> admin = adminRows.get(i);
            if (admin.get(1).equals(admin_em) && admin.get(2).equals(admin_ps)){
                proceed = true;
                admin_id = admin.getFirst();
                break;
            }
        }
        if (proceed){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_dashboard.fxml"));
            Parent root = fxmlLoader.load();
            adminDashboard = fxmlLoader.getController();
            adminDashboard.setAdmin(admin_id);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);

            stage.show();
            Stage this_stage = (Stage) adminBtn.getScene().getWindow();
            this_stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Administrator Account");
            alert.setTitle("Invalid Administrator Error");
            alert.show();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        shutdown_img.setOnMouseClicked(mouseEvent -> shutdown());
        studentID.setStyle("-fx-text-fill: #203864;");
        adminEmail.setStyle("-fx-text-fill: #203864;");
        adminPass.setStyle("-fx-text-fill: #203864;");
    }
}