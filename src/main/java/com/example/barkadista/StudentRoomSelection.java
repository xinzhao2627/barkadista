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

public class StudentRoomSelection implements Initializable {
    public StackPane StackRoot;
    @FXML
    private AnchorPane anchorMain;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrev;
    @FXML
    Label room_label;
    @FXML
    private AnchorPane report_prompt;
    @FXML
    private AnchorPane comp_settings_pane;
    String user;

    StudentFrontEndMethods processor = new StudentFrontEndMethods();
    AdminRoomSelection adminRoomSelection;
    @FXML
    private ImageView home_img;
    @FXML
    private ImageView shutdown_img;
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
    public void setUser(String studentID) {
        this.user = studentID;
        processor.setUser(this.user);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home_img.setOnMouseClicked(mouseEvent -> logout());
        shutdown_img.setOnMouseClicked(mouseEvent -> shutdown());
        // slot settings
        ArrayList<String> rooms;
        ArrayList<ArrayList<ArrayList<String>>> room_with_comp_with_slot = new ArrayList<>();

        try {
            processor.setCurrent_room_label(room_label);
            processor.setCompReportPane(report_prompt);
            ArrayList<ArrayList<String>> comp_rows = query.readData("computer");
            rooms = query.getColumnData("room", "room_number");


            // sort computer by rooms
            for (int i = 0; i < rooms.size(); i++){
                ArrayList<ArrayList<String>> tmp_room = new ArrayList<>();
                // if one of the computers is equal to one of the rooms...
                for (ArrayList<String> tmp_comp : comp_rows){
                    if (tmp_comp.getFirst().equals(rooms.get(i))){
                        // add the whole computer to the room
                        tmp_room.add(tmp_comp);
                    }
                }
                room_with_comp_with_slot.add(tmp_room);
            }

            int xpos = 0;
            // generate rooms
            for (ArrayList<ArrayList<String>> tmp_room: room_with_comp_with_slot ){
                // tmp_room are one of the rooms with computers
                // TODO get the room number
                /* assuming that tmp_room is properly sorted, we can just get the room number on one of the computers */
                String room = null;

                // TODO get the computer slots for this tmp_room
                ArrayList<String> comp_slot = new ArrayList<>();

                for (ArrayList<String> tmp_comp: tmp_room){
                    room = tmp_comp.getFirst();  // just get the room, each loop has the same room number anyway
                    comp_slot.add(tmp_comp.get(2)); // get their corresponding computer slots
                }
                processor.generate_room(comp_slot, anchorMain, room, xpos*1080);
                xpos++;
            }
            processor.setCompSettingsPane(comp_settings_pane);
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


}
