package com.example.barkadista;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminReports implements Initializable {
    public AnchorPane anchorMain;
    @FXML
    private AnchorPane Dashboard;

    @FXML
    private AnchorPane ReportsManager;

    @FXML
    private AnchorPane RoomSelection;
    @FXML
    private TableColumn<ArchivedData , String> archcol_Content;

    @FXML
    private TableColumn<ArchivedData , String> archcol_Date;

    @FXML
    private TableColumn<ArchivedData , Integer> archcol_Pcnum;

    @FXML
    private TableColumn<ArchivedData , Integer> archcol_Reportnum;
    @FXML
    private TableView<ArchivedData> table_Archived;

    @FXML
    private TableView<ReportData> table_Report;

    @FXML
    private TableColumn<ArchivedData , Integer> archcol_Roomnum;

    @FXML
    private TableColumn<ArchivedData , String> archcol_Submittee;

    @FXML
    private TableColumn<ArchivedData , String>archcol_comment;

    @FXML
    private TableColumn<ReportData, String> col_Content;

    @FXML
    private TableColumn<ReportData, String> col_Date;
    @FXML
    private TableColumn<ReportData, String> col_comment;

    @FXML
    private TableColumn<ReportData, Integer> col_Pcnum;

    @FXML
    private TableColumn<ReportData, Integer> col_Reportnum;

    @FXML
    private TableColumn<ReportData, Integer> col_Roomnum;

    @FXML
    private TableColumn<ReportData, String> col_Submittee;
    @FXML
    private ImageView home_img;
    @FXML
    private ImageView shutdown_img;
    String admin ="";
    Query query = new Query();
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
    ObservableList<ReportData> reports_initialData() throws SQLException, ClassNotFoundException {
        ArrayList<ArrayList<String>> reports_general_list = query.readData("report");
        ArrayList<ReportData> reports_data_list = new ArrayList<>();
        for (ArrayList<String> tmp_reports : reports_general_list){
            int room_num = Integer.parseInt(tmp_reports.getFirst());
            int comp_num = Integer.parseInt(tmp_reports.get(1));
            int rep_num = Integer.parseInt(tmp_reports.get(2));
            String cont = tmp_reports.get(3);
            String comment_user = tmp_reports.get(4);
            String submitter = tmp_reports.get(5);
            String rep_date = tmp_reports.get(6);
            ReportData reportData = new ReportData(room_num, comp_num, rep_num, cont, comment_user, submitter, rep_date);
            reports_data_list.add(reportData);
        }
        return FXCollections.observableArrayList(reports_data_list);
    }
    ObservableList<ArchivedData> archived_initialData() throws SQLException, ClassNotFoundException {
        ArrayList<ArrayList<String>> archive_general_list = query.readData("archive_report");
        ArrayList<ArchivedData> archived_data_list = new ArrayList<>();
        for (ArrayList<String> tmp_archived : archive_general_list){
            int room_num = Integer.parseInt(tmp_archived.getFirst());
            int comp_num = Integer.parseInt(tmp_archived.get(1));
            int rep_num = Integer.parseInt(tmp_archived.get(2));
            String cont = tmp_archived.get(3);
            String comment_user = tmp_archived.get(4);
            String submitter = tmp_archived.get(5);
            String rep_date = tmp_archived.get(6);
            ArchivedData archivedData = new ArchivedData(room_num, comp_num, rep_num, cont, comment_user, submitter, rep_date);
            archived_data_list.add(archivedData);
        }
        return FXCollections.observableArrayList(archived_data_list);
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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        home_img.setOnMouseClicked(mouseEvent -> logout());
        shutdown_img.setOnMouseClicked(mouseEvent -> shutdown());
        try {
            col_Roomnum.setCellValueFactory(new PropertyValueFactory<>("room_number"));
            col_Pcnum.setCellValueFactory(new PropertyValueFactory<>("computer_number"));
            col_Reportnum.setCellValueFactory(new PropertyValueFactory<>("report_number"));
            col_Content.setCellValueFactory(new PropertyValueFactory<>("content_summary"));
            col_comment.setCellValueFactory(new PropertyValueFactory<>("user_comment"));
            col_Submittee.setCellValueFactory(new PropertyValueFactory<>("submittee"));
            col_Date.setCellValueFactory(new PropertyValueFactory<>("date"));
            table_Report.setItems(reports_initialData());
            for (TableColumn col : table_Report.getColumns()){
                col.setStyle("-fx-alignment: CENTER; -fx-font-family: Corbel; -fx-background-color: transparent");
            }
            table_Report.setRowFactory(tv -> { // set each rows clickable
                TableRow<ReportData> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getClickCount() == 2) { // Double-click detected
                        ReportData rowData = row.getItem();
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Archive Report");
                        alert.setContentText("Are you sure you want to archive this report?");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent()){
                            if (result.get() == ButtonType.OK){
                                try {
                                    System.out.println("ok is clicked");
                                    query.deleteReport(rowData.getReport_number()); // place to archived
                                    table_Report.setItems(reports_initialData());
                                    table_Archived.setItems(archived_initialData());
                                } catch (SQLException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                            }
                        }
                    }
                });
                return row;
            });

            archcol_Roomnum.setCellValueFactory(new PropertyValueFactory<>("room_number"));
            archcol_Pcnum.setCellValueFactory(new PropertyValueFactory<>("computer_number"));
            archcol_Reportnum.setCellValueFactory(new PropertyValueFactory<>("report_number"));
            archcol_Content.setCellValueFactory(new PropertyValueFactory<>("content_summary"));
            archcol_comment.setCellValueFactory(new PropertyValueFactory<>("user_comment"));
            archcol_Submittee.setCellValueFactory(new PropertyValueFactory<>("submittee"));
            archcol_Date.setCellValueFactory(new PropertyValueFactory<>("date"));
            table_Archived.setItems(archived_initialData());

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
