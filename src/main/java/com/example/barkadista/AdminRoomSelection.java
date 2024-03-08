package com.example.barkadista;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class AdminRoomSelection implements Initializable {
    @FXML
    private AnchorPane Dashboard;

    @FXML
    private AnchorPane ReportsManager;

    @FXML
    private AnchorPane RoomSelection;
    public StackPane StackRoot;
    @FXML
    private AnchorPane anchorMain;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    Label label1;
    @FXML
    Label room_label;
    @FXML
    private AnchorPane report_prompt;
    @FXML
    private MenuButton roomSettingsPane;
    @FXML
    private MenuItem item_create;
    @FXML
    private MenuItem item_delete;
    @FXML
    private TextField room_textfield;
    @FXML
    private AnchorPane create_room_pane;
    @FXML
    private AnchorPane comp_settings_pane;
    @FXML
    private ImageView home_img;
    @FXML
    private ImageView shutdown_img;
    String user ="";

    FrontEndMethods processor = new FrontEndMethods();
    AdminRoomSelection adminRoomSelection;
    int show = 0;
    Query query = new Query();
    String previous_room = "";
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

    public void logout(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainFrame-view.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Main_frame_controller mainFrameController= fxmlLoader.getController();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        Stage this_stage = (Stage)home_img.getScene().getWindow();
        this_stage.close();
    }

    public void proceedRoomSelection(MouseEvent mouseEvent){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_room_selection.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AdminRoomSelection adminRoomSelection= fxmlLoader.getController();
        adminRoomSelection.setUser(user);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        Stage this_stage = (Stage)RoomSelection.getScene().getWindow();
        this_stage.close();
    }
    public void proceedDashboard(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_dashboard.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AdminDashboard adminDashboard= fxmlLoader.getController();
        adminDashboard.setAdmin(user);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        Stage this_stage = (Stage)Dashboard.getScene().getWindow();
        this_stage.close();
    }

    public void proceedReportsManager(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_reports.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AdminReports adminReports= fxmlLoader.getController();
        adminReports.setAdmin(user);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        Stage this_stage = (Stage)ReportsManager.getScene().getWindow();
        this_stage.close();
    }

    public void next() throws SQLException, ClassNotFoundException {
        // if next is clicked, place the current room behind and place the next roomo in the current's position
        String current_room = getroom(show);
        show++;
        String next_room = getroom(show);
        // if there is a next room
        if (!next_room.equals(current_room)){
            previous_room = current_room;
            // get the id of the grid pane
            Node next_node = anchorMain.lookup("#"+next_room);
            room_label.setText(next_node.getId());
            processor.setCurrent_room(next_node.getId());
            System.out.println("The room behind is: " + previous_room);

            // add transition to the node
            // get all the rooms first (the grid panes)
            for (Node n : anchorMain.getChildren()){
                if (n instanceof  GridPane){
                    processor.moveAnimation(0.5, n, -1080);
                }
            }
        }
    }

    public void back() throws SQLException, ClassNotFoundException {
        // if back is clicked, move the current room to the front and the previous room to the current room's position
        String current_room = getroom(show);
        // previous room is already defined
        show--;
        String next_room = getroom(show);
        // if there is a previous room
        if (!next_room.equals(current_room)){
            // get the grid pane id of the previous room
            Node next_node = anchorMain.lookup("#"+next_room);
            room_label.setText(next_node.getId());
            processor.setCurrent_room(next_node.getId());
            for (Node n : anchorMain.getChildren()){
                if (n instanceof  GridPane){
                    processor.moveAnimation(0.5, n, 1080);
                }
            }
        }
    }

    public String getroom(int v) throws SQLException, ClassNotFoundException {
        // select all room numbers
        ArrayList<String> room_list = query.getColumnData("room", "room_number");
        Dictionary<Integer, String> dict= new Hashtable<>();
        if (!room_list.isEmpty()){
            //assign rooms with their order number
            for (int i = 0; i < room_list.size(); i++){
                dict.put(i, room_list.get(i));
            }
            String room = dict.get(v);

            // if you cannot find the next room, just return the current room
            if (room == null){
                if (show < 0){
                    show++;
                    System.out.println("Already at min room");
                } else {
                    show --;
                    System.out.println("Already at max room");
                }

                return getroom(show);
            }
            // else show the next room
            else {
                return room;
            }
        } return "0";
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home_img.setOnMouseClicked(mouseEvent -> logout());
        shutdown_img.setOnMouseClicked(mouseEvent -> shutdown());
        // slot settings
        ArrayList<String> rooms;

        try {
            processor.setCurrent_room_label(room_label);
            processor.setCompReportPane(report_prompt);
            ArrayList<ArrayList<String>> comp_rows = query.readData("computer");
            rooms = query.getColumnData("room", "room_number");

            // CRUD OF ROOM
            processor.addRoomAction(create_room_pane, rooms, room_textfield);

            int xpos = 0;

            for (String room: rooms){// get the rooms first
                // get the computer in that room
                ArrayList<ArrayList<String>> comp_in_room = query.readData("computer","room_number = "+room);
                // get those computer's slots
                ArrayList<String> computer_slots = new ArrayList<>();
                for (ArrayList<String> comp : comp_in_room){
                    String slot = comp.get(2);
                    computer_slots.add(slot);
                }
                // generate rooms in ui
                processor.generate_room(computer_slots,anchorMain,room,xpos*1080 );
                xpos++;
            }

            processor.setCompSettingsPane(comp_settings_pane);
            // action when the 'create room' is clicked
            item_create.setOnAction(actionEvent -> {
                create_room_pane.toFront();
                create_room_pane.setVisible(true);
            });
            item_delete.setOnAction(actionEvent -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Delete Room");
                alert.setContentText("Warning, you are about to delete this room");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()){
                    if (result.get()==ButtonType.OK){
                        // frontend
                        ArrayList<ArrayList<String>> tbd_computer_list;
                        try {
                            tbd_computer_list = query.readData("computer", "room_number = "+room_label.getText());
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println(tbd_computer_list);
                        if (!tbd_computer_list.isEmpty()){
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("ROOM DELETION ERROR");
                            alert2.setContentText("THERE ARE STILL COMPUTERS IN THE ROOM, PLEASE \n MOVE THE COMPUTERS TO ANOTHER ROOM \n OR DELETE THE COMPUTERS");
                            alert2.show();
                        } else {
                            GridPane del_room = (GridPane) anchorMain.lookup("#"+room_label.getText());
                            int del_room_num = Integer.parseInt(del_room.getId());

                            //backend
                            processor.deleteRoomAction(del_room_num);

                            //refresh
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_room_selection.fxml"));
                            Parent root = null;
                            try {
                                root = fxmlLoader.load();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            adminRoomSelection= fxmlLoader.getController();
                            adminRoomSelection.setUser(user);
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.show();
                            Stage this_stage = (Stage)anchorMain.getScene().getWindow();
                            this_stage.close();
                        }
                    }
                }
            });
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
        //TODO place the rooms at their initial location
        int multiplier = 0;
        for (String room: rooms){
            Node node = anchorMain.lookup("#"+room);
            processor.moveAnimation(0.1, node, multiplier*1080);
            multiplier++;
        }
        // initial display
        if (!rooms.isEmpty()){
            room_label.setText(rooms.getFirst());
        }


        // visual design
        btnNext.toFront();
        btnPrev.toFront();

        // set the anchor main in the processor so we can edit the ui from another class file
        processor.setMain(anchorMain);
    }

    public void setUser(String admin) {
        user = admin;
        processor.setUser(admin);
    }
}
