package com.example.barkadista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class AdminDashboard implements Initializable {
    public AnchorPane anchorMain;
    @FXML
    private AnchorPane Dashboard;

    @FXML
    private AnchorPane ReportsManager;

    @FXML
    private AnchorPane RoomSelection;
    @FXML
    private BarChart<String, Number> roomGraph;
    @FXML
    private PieChart overallChart;
    @FXML
    private Label overall_active_label;

    @FXML
    private Label overall_major_label;

    @FXML
    private Label overall_minor_label;

    @FXML
    private Label overall_report_label;

    @FXML
    private Label overall_unusable_label;

    @FXML
    private AnchorPane red_report;
    @FXML
    private Label warning_label;
    @FXML
    private ImageView symbol_report;

    @FXML
    private Label room_report_label;

    @FXML
    private Button room_next_btn;

    @FXML
    private Button room_prev_btn;
    @FXML
    private Label active_pc_label;

    @FXML
    private Label inactive_pc_label;
    @FXML
    private ImageView home_img;
    @FXML
    private ImageView shutdown_img;


    String admin = "";
    Query query = new Query();
    int show = 0;
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
    public void setAdmin(String admin) {
        this.admin = admin;
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
        adminRoomSelection.setUser(admin);
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
        adminDashboard.setAdmin(admin);
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
        adminReports.setAdmin(admin);
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        Stage this_stage = (Stage)ReportsManager.getScene().getWindow();
        this_stage.close();
    }
    private void showRoomGraph(String onscreenRoom) throws SQLException, ClassNotFoundException {

        ArrayList<String> comps_conditions = query.getColumnData("computer", "condition", "room_number = "+onscreenRoom);
        int active = 0;
        int activeMinor = 0;
        int activeMajor = 0;
        int unusable = 0;
        for (String tmp_condition : comps_conditions){
            if (tmp_condition.equals("GOOD CONDITION")){
                active++;
            } else if (tmp_condition.equals("WITH MINOR ISSUE")) {
                activeMinor++;
            } else if (tmp_condition.equals("WITH MAJOR ISSUE")) {
                activeMajor++;
            } else if (tmp_condition.equals("BAD CONDITION")) {
                unusable++;
            }
        }
        // --- BAR CHART SECTION ---
        // set y axis
        NumberAxis y_axis = (NumberAxis) roomGraph.getYAxis();
        y_axis.setAutoRanging(false);
        y_axis.setLowerBound(0); // initial bound
        y_axis.setUpperBound(40);
        y_axis.setTickUnit(5);

        // declare series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Room "+onscreenRoom);

        series.getData().add(new XYChart.Data<>("Good condition", active));
        series.getData().add(new XYChart.Data<>("Minor issue", activeMinor));
        series.getData().add(new XYChart.Data<>("Major issue", activeMajor));
        series.getData().add(new XYChart.Data<>("Bad Condition", unusable));

        ObservableList<XYChart.Series<String, Number>> one_series= FXCollections.observableArrayList();
        one_series.add(series);
        // set data
        roomGraph.setData(one_series);

        // style data
        for (XYChart.Data<String, Number> data: series.getData()){

            if (data.getNode()!= null){
                if (data.getXValue().equals("Good condition")){
                    data.getNode().setStyle("-fx-bar-fill: #50ffb1;");
                } else if (data.getXValue().equals("Minor issue")){
                    data.getNode().setStyle("-fx-bar-fill: #fff200;");
                } else if (data.getXValue().equals("Major issue")){
                    data.getNode().setStyle("-fx-bar-fill: #f4a259;");
                } else if (data.getXValue().equals("Bad condition")){
                    data.getNode().setStyle("-fx-bar-fill: #ee6055;");
                }
            }
        }
        // --- END OF BAR CHART SECTION ---
        int total_reports_per_room = query.getCountData("report", "report_number", "room_number = "+onscreenRoom);
        room_report_label.setText(String.valueOf(total_reports_per_room));
    }

    private void next() throws SQLException, ClassNotFoundException {
        // if next is clicked, place the current room behind and place the next roomo in the current's position
        String current_room = getroom(show);
        show++;
        String next_room = getroom(show);

        // if there is a next room
        if (!next_room.equals(current_room)){
            showRoomGraph(next_room);
        }
    }
    private void prev() throws SQLException, ClassNotFoundException {
        String current_room = getroom(show);
        // previous room is already defined
        show--;
        String next_room = getroom(show);

        // if there is a previous room
        if (!next_room.equals(current_room)){
            showRoomGraph(next_room);
        }
    }

    private String getroom(int roomAt) throws SQLException, ClassNotFoundException {
        // select all room numbers
        ArrayList<String> room_list = query.getColumnData("room", "room_number");
        Dictionary<Integer, String> dict= new Hashtable<>();
        if (!room_list.isEmpty()){
            //assign rooms with their order number
            for (int i = 0; i < room_list.size(); i++){
                dict.put(i, room_list.get(i));
            }
            String room = dict.get(roomAt);

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

        room_next_btn.setOnMouseClicked(mouseEvent -> {
            try {
                next();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        room_prev_btn.setOnMouseClicked(mouseEvent -> {
            try {
                prev();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });


        try {

            // show first graph
            ArrayList<ArrayList<String>> rooms_complete = query.readData("room");
            if (!rooms_complete.isEmpty()){
                showRoomGraph(rooms_complete.getFirst().getFirst());
            }


            // --- PIE CHART SECTION ---
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            ArrayList<String> conditions_list = query.getColumnData("computer", "condition");
            ArrayList<String> status_list = query.getColumnData("computer", "status");
            int pc_active = 0;
            int pc_inactive = 0;
            for (String tmp_status: status_list){
                if (tmp_status.equals("0")){
                    pc_active++;
                } else if (tmp_status.equals("1")) {
                    pc_inactive++;
                }
            }
            active_pc_label.setText(String.valueOf(pc_active));
            inactive_pc_label.setText(String.valueOf(pc_inactive));


            int total_reports = query.getTotalReport();
            int active = 0;
            int activeMinor = 0;
            int activeMajor = 0;
            int unusable = 0;
            for (String condition: conditions_list){
                if (condition.equals("GOOD CONDITION")){
                    active++;
                } else if (condition.equals("WITH MINOR ISSUE")) {
                    activeMinor++;
                } else if (condition.equals("WITH MAJOR ISSUE")){
                    activeMajor++;
                } else if (condition.equals("BAD CONDITION")) {
                    unusable++;
                }
            }

            pieData.addAll(new PieChart.Data("Good Condition", active),
                    new PieChart.Data("Minor Issue", activeMinor),
                    new PieChart.Data("Major Issue", activeMajor),
                    new PieChart.Data("Bad Condition", unusable)
            );

            overallChart.setData(pieData);
            overallChart.getData().get(0).getNode().setStyle("-fx-pie-color: #50ffb1;");
            overallChart.getData().get(1).getNode().setStyle("-fx-pie-color: #fff200;");
            overallChart.getData().get(2).getNode().setStyle("-fx-pie-color: #f4a259;");
            overallChart.getData().get(3).getNode().setStyle("-fx-pie-color: #ee6055;");
            overallChart.setStyle("-fx-pie-border-color: transparent;");

            overall_active_label.setText(String.valueOf(active));
            overall_minor_label.setText(String.valueOf(activeMinor));
            overall_major_label.setText(String.valueOf(activeMajor));
            overall_unusable_label.setText(String.valueOf(unusable));
            if (total_reports == 0){
                Image image = new Image(getClass().getResourceAsStream("/com/example/barkadista/green_safe.png"));
                symbol_report.setImage(image);
                overall_report_label.setText("There are currently no computer issues reported in the system. To file a ticket, please go to the room selection");
                overall_report_label.setTextFill(Color.web("#385723"));
                warning_label.setText("No active reports");
                warning_label.setTextFill(Color.web("#385723"));
                red_report.setStyle("-fx-background-color: #00FFAB; -fx-border-color: #14C38E; -fx-border-width: 2.5");


            } else {
                overall_report_label.setText(total_reports+" tickets have been submitted reporting issues found on various computers within the system.");

            }

            // --- END OF PIE CHART SECTION ---
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
