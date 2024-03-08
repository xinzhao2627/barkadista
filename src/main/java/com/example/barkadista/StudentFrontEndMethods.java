package com.example.barkadista;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class StudentFrontEndMethods {

    // generate a room with a template

    Query query = new Query();

    AnchorPane main = new AnchorPane();
    AnchorPane comp_settings_pane = new AnchorPane();
    String current_room = "";
    ImageView candidate_pc;
    GridPane candidate_room;
    Label candidate_slot;
    ToggleGroup candidate_statuses_group = new ToggleGroup();
    Label label_current_room;
    AnchorPane reportPrompt;
    String user;

    public void generate_room(ArrayList<String> computer_slots, AnchorPane anchorMain, String room_num, int xpos) throws SQLException, ClassNotFoundException {

        GridPane grid_n = new GridPane();
        grid_n.setId(room_num); // room id
        grid_n.setLayoutX(140.0);
        grid_n.setLayoutY(150.0);
        grid_n.prefHeight(405.0);
        grid_n.prefWidth(754.0);
        grid_n.setAlignment(Pos.CENTER);
        anchorMain.getChildren().add(grid_n);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setHalignment(HPos.CENTER);
        columnConstraints.setMinWidth(60.0);
        columnConstraints.setPrefWidth(100.0);
        columnConstraints.setPercentWidth(-1);
        columnConstraints.setMaxWidth(100);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setVgrow(Priority.SOMETIMES);
        rowConstraints.setMinHeight(60.0);
        rowConstraints.setPrefHeight(80.0);
        rowConstraints.setPercentHeight(-1);
        rowConstraints.setMaxHeight(100);

        for (int row = 0; row < 5; row++){
            grid_n.getRowConstraints().add(rowConstraints);
        }

        for (int col = 0; col < 8; col++){
            grid_n.getColumnConstraints().add(columnConstraints);
        }

        int slots = 1;
        ImageView imageView;
        Label label_text;
        for (int i = 0; i < grid_n.getColumnCount(); i++){
            for (int j = 0; j < grid_n.getRowCount(); j++){

                // FOR NO PC add a space

                // --- DESIGN START (text format) ---
                label_text = new Label();
                label_text.setStyle("-fx-border-style: segments(10, 10, 10, 10) line-cap round");
                label_text.setPrefHeight(60);
                label_text.setPrefWidth(60);
                label_text.setAlignment(Pos.CENTER);
                label_text.isWrapText();
                label_text.setText(String.valueOf(slots));
                label_text.setId(String.valueOf(slots)); // slot_id
                String s = label_text.getText();  // slot_id
                // --- DESIGN END ---

                grid_n.add(label_text, i,j);

                // this is just for formatting the columns to be properly visualized, only change the node here (label_text)

                if (i % 2 == 0){
                    GridPane.setMargin(label_text, new Insets(0, 0, 0, 15));
                } else {
                    GridPane.setMargin(label_text, new Insets(0, 15, 0, 0));
                }

                // find the computer that has the same slot_id as the generated slot number
                for (String value : computer_slots) {

                    // if there is a computer in that slot...
                    if (label_text.getText().equals(value)) {

                        // -    -- DESIGN START (image format) ---
                        Image image = new Image(getClass().getResourceAsStream("/com/example/barkadista/colored pc.png"));
                        imageView = new ImageView(image);
                        imageView.setFitHeight(60);
                        imageView.setFitWidth(60);
                        //backend
                        ArrayList<String> specific_pc = query.getColumnData("computer", "computer_number","room_number = "+room_num+" and room_slot = "+value);
                        imageView.setId(specific_pc.getFirst());
                        setCompOnclick(imageView, grid_n, label_text); // make imageview clickable
                        // --- DESIGN END ---

                        // add an image to that slot
                        grid_n.add(imageView, i,j);


                        // this is just for formatting the columns to be properly visualized, only change the node here (label_text)
                        if (i % 2 == 0){
                            GridPane.setMargin(imageView, new Insets(0, 0, 0, 15));
                        } else {
                            GridPane.setMargin(imageView, new Insets(0, 15, 0, 0));
                        }
                        grid_n.getChildren().remove(label_text);
                        break;
                    }
                }
                slots++;
            }
        }
    }

    // report the pc, add ui

    public void setUser(String user) {
        this.user = user;
    }

    public void setCompReportPane(AnchorPane reportPrompt) {
        this.reportPrompt = reportPrompt;

        // set comp status radio button
        RadioButton rb_active = (RadioButton) reportPrompt.lookup("#report_pcActive_rb");
        RadioButton rb_inactive = (RadioButton) reportPrompt.lookup("#report_pcInactive_rb");
        rb_active.setToggleGroup(candidate_statuses_group);
        rb_inactive.setToggleGroup(candidate_statuses_group);

        // set component choice box
        ChoiceBox<String> report_pcParts = (ChoiceBox<String>) reportPrompt.lookup("#report_pcParts");
        // TODO backend (query the pc parts of the candidate computer)
        String[] parts_list = query.getComponents();
        ObservableList<String> parts_choices = FXCollections.observableArrayList(parts_list);
        report_pcParts.setItems(parts_choices);

        // set severity choice box
        ChoiceBox<String> report_pcSeverity = (ChoiceBox<String>) reportPrompt.lookup("#report_pcSeverity");
        String[] severities = {"Minor Issue", "Major Issue", "Unusable"};
        ObservableList<String> severities_choices = FXCollections.observableArrayList(severities);
        report_pcSeverity.getItems().addAll(severities_choices);

    }
    public void reportComp(){
        reportPrompt.setVisible(true);
        reportPrompt.toFront();
        // Text Fields
        TextField compID = (TextField) reportPrompt.lookup("#report_pcID_TF");
        TextField roomNum = (TextField) reportPrompt.lookup("#report_pcRoom_TF");
        TextField reportDate = (TextField) reportPrompt.lookup("#report_pcDate_TF");
        TextField  userTF= (TextField) reportPrompt.lookup("#report_pcUser_TF");

        userTF.setText(user);
        reportDate.setText(LocalDate.now().toString());
        roomNum.setText(candidate_room.getId());
        compID.setText(candidate_pc.getId());
    }
    // make the pc delete and reports clickable
    public void setCompOnclick(ImageView pc, GridPane room, Label slot){
        pc.setOnMouseClicked(mouseEvent -> {
            comp_settings_pane.toFront();
            comp_settings_pane.setVisible(true);

            candidate_pc = pc;
            candidate_room = room;
            candidate_slot = slot;

            // provide info overview on the computer
            Label compID_lb = (Label) comp_settings_pane.lookup("#setting_comp_ID_lb");
            Label compST_lb = (Label) comp_settings_pane.lookup("#setting_comp_Status_lb");
            Label compCD_lb = (Label) comp_settings_pane.lookup("#setting_comp_Condition_lb");
            Label compSV_lb = (Label) comp_settings_pane.lookup("#setting_comp_component_word");

            compID_lb.setText(candidate_pc.getId());


            //TODO backend section
            ChoiceBox<String> comp_CM_cb = (ChoiceBox<String>) comp_settings_pane.lookup("#setting_comp_component_cb");
            comp_CM_cb.getItems().clear();
            comp_CM_cb.setValue(null);
            // TODO query the components list of the PC and put to arraylist
            ArrayList<ArrayList<String>> components_general;
            try {
                components_general= query.readData("components", "computer_number = "+candidate_pc.getId());
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
            ArrayList<String> components_list = new ArrayList<>();
            for (ArrayList<String> tmp_component_list : components_general){
                // if one of the component rows belongs to the pc whose info is viewed (candidate_pc)
                if (tmp_component_list.getFirst().equals(candidate_pc.getId())){
                    components_list.add(tmp_component_list.get(1));
                }
            }
            ObservableList<String> components_choices = FXCollections.observableArrayList(components_list);
            comp_CM_cb.setItems(components_choices);
            String component_severity = "";
            comp_CM_cb.setOnAction(actionEvent -> {
                String target_component = comp_CM_cb.getValue(); // get the selected component
                ArrayList<ArrayList<String>> target_component_severity; // sverity
                try { // find that component in the database
                    target_component_severity = query.readData("components", "component_name = '"+target_component+"' and computer_number = "+candidate_pc.getId());
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }

                if (target_component_severity.iterator().hasNext()){
                    String word_description = target_component_severity.getFirst().get(2);
                    compSV_lb.setText(word_description);
                }
            });
            ArrayList<ArrayList<String>> pc_stat_con;

            try {
                pc_stat_con = query.readData("computer","room_number = "+candidate_room.getId()+" and room_slot = "+candidate_slot.getText());
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
            String status = pc_stat_con.getFirst().get(3);
            String condition = pc_stat_con.getFirst().get(4);

            if (status.equals("0")){
                status = "Active";
            } else if (status.equals("1")){
                status = "Inactive";
            }

            switch(condition) {
                case "0":
                    condition ="Good";
                    break;
                case "1":
                    condition = "Minor Issue";
                    break;

                case "2":
                    condition = "Major issue";
                    break;

                case "3":
                    condition = "Unusable";
                    break;
            }

            compST_lb.setText(status); // computer status get from query
            compCD_lb.setText(condition); // computer condition get from query
        });
    }
    public void setCurrent_room_label(Label label_current_room){
        this.label_current_room = label_current_room;
    }

    public void setCompSettingsPane(AnchorPane compSettingsPane) {
        this.comp_settings_pane = compSettingsPane;
        for (Node n : comp_settings_pane.getChildren()){
            if (n instanceof Button){
                String btn_id = n.getId();
                if (btn_id.equals("comp_report_Btn")) { // if report is clicked
                    n.setOnMouseClicked(mouseEvent1 -> {
                        compSettingsPane.setVisible(false);
                        reportComp();
                        ImageView report_close_btn = (ImageView) reportPrompt.lookup("#report_close_btn");
                        report_close_btn.setOnMouseClicked(mouseEvent2 -> {
                            reportPrompt.setVisible(false);
                        });
                        Button report_submit_btn = (Button) reportPrompt.lookup("#report_submit_btn");
                        report_submit_btn.setOnMouseClicked(mouseEvent2 -> {

                            Toggle selectedToggle = candidate_statuses_group.getSelectedToggle();
                            String selectedComputerStatus ="";

                            if (selectedToggle!= null){
                                RadioButton selectedRadioButton = (RadioButton) selectedToggle;
                                selectedComputerStatus = selectedRadioButton.getText();
                            }

                            ChoiceBox<String> report_pcParts = (ChoiceBox<String>) reportPrompt.lookup("#report_pcParts");
                            ChoiceBox<String> report_pcSeverity = (ChoiceBox<String>) reportPrompt.lookup("#report_pcSeverity");
                            TextArea report_userComment = (TextArea) reportPrompt.lookup("#report_pcContent");

                            TextField report_date = (TextField) reportPrompt.lookup("#report_pcDate_TF");
                            TextField report_userId = (TextField) reportPrompt.lookup("#report_pcUser_TF");
                            TextField report_roomNum = (TextField) reportPrompt.lookup("#report_pcRoom_TF");
                            TextField report_compID = (TextField) reportPrompt.lookup("#report_pcID_TF");

                            String date = report_date.getText();
                            String userId = report_userId.getText();
                            String roomNum = report_roomNum.getText();
                            String compID = report_compID.getText();
                            String selectedComponentSeverity = report_pcSeverity.getValue();
                            String selectedComponent = report_pcParts.getValue();
                            String userComment = report_userComment.getText();

                            String content_summary = selectedComputerStatus+"_"+selectedComponent+"_"+selectedComponentSeverity;

                            try {
                                query.addReport(Integer.parseInt(roomNum), Integer.parseInt(compID), content_summary, userComment, userId, date);
                            } catch (ClassNotFoundException | SQLException e) {
                                throw new RuntimeException(e);
                            }
                            reportPrompt.setVisible(false);
                        });

                    });
                }
            }else if (n instanceof ImageView) {
                if (n.getId().equals("comp_close_Btn")){
                    n.setOnMouseClicked(mouseEvent1 -> {
                        comp_settings_pane.setVisible(false);
                    });
                }
            }
        }
    }

    public void moveAnimation(double duration, Node node, double xpos){
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(duration), node);
        translateTransition.setByX(xpos);
        translateTransition.play();
    }



    /* ---- ROOM SECTION ---- */
    public void setCurrent_room(String current_room) {
        this.current_room = current_room;
    }

    public void setMain(AnchorPane main) {
        this.main = main;
    }
}



