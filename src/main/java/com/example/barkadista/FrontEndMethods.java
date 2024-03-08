package com.example.barkadista;

import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class FrontEndMethods {

    // generate a room with a template

    Query query = new Query();
    AnchorPane promptPane = new AnchorPane();
    AnchorPane createRoomPane = new AnchorPane();
    AnchorPane main = new AnchorPane();
    AnchorPane comp_settings_pane = new AnchorPane();
    String current_room = "";
    MenuButton roomSettingsPane = new MenuButton();
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
                label_text.setOnMouseClicked(mouseEvent -> {
                    try {
                        openPrompt(s);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
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

    // this is the onclick button when the slot is clicked
    public void openPrompt(String slot_num) throws SQLException, ClassNotFoundException {
        String room_num = label_current_room.getText();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ADD COMPUTER");
        alert.setContentText("Would you like to add a computer on room "+room_num+" on slot "+slot_num+" ?");
        Optional <ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK){ // if proceed
            // frontend add computer
            GridPane room_pane = (GridPane) main.lookup("#"+room_num); // find the room
            Label slot_label= (Label)room_pane.lookup("#"+slot_num); // find the slot in that room
            int rowIndex = GridPane.getRowIndex(slot_label);
            int colIndex = GridPane.getColumnIndex(slot_label);
            Image img_pc = new Image(getClass().getResourceAsStream("/com/example/barkadista/colored pc.png"));
            ImageView pc = new ImageView(img_pc);
            pc.setFitHeight(60);
            pc.setFitWidth(60);
            GridPane.setRowIndex(pc, rowIndex);
            GridPane.setColumnIndex(pc, colIndex);
            room_pane.getChildren().add(pc);
            room_pane.getChildren().remove(slot_label);
            setCompOnclick(pc, room_pane, slot_label); // make the newly generated pc clickable
            if (colIndex % 2 == 0){
                GridPane.setMargin(pc, new Insets(0, 0, 0, 15));
            } else {
                GridPane.setMargin(pc, new Insets(0, 15, 0, 0));
            }
            // TODO backend: (add pc) using the query
            if (!room_num.isEmpty() || !slot_num.isEmpty()){
                int int_room_num = Integer.parseInt(room_num);
                int int_slot_num = Integer.parseInt(slot_num);
                // TODO add int room_num and slot_num, set everything as default (0)
                // query
                /* --- HERE --- */
                query.addComputer(int_room_num, int_slot_num);
                ArrayList<String> specific_pc = query.getColumnData("computer", "computer_number","room_number = "+room_num+" and room_slot = "+slot_num);
                pc.setId(specific_pc.getFirst());
            }
        }


    }

    // delete the pc, error dialogue
    public void deleteComp() throws SQLException, ClassNotFoundException {
        int rowIndex = GridPane.getRowIndex(candidate_pc);
        int colIndex = GridPane.getColumnIndex(candidate_pc);
        System.out.println(rowIndex + " " + colIndex);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Computer");
        alert.setContentText("You are about to delete this computer, proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK ){
            //frontend, change the image view to a labeltext
            GridPane.setRowIndex(candidate_slot, rowIndex);
            GridPane.setColumnIndex(candidate_slot, colIndex);
            candidate_room.getChildren().add(candidate_slot);
            candidate_room.getChildren().remove(candidate_pc);
            comp_settings_pane.setVisible(false);

            // TODO backend (delete pc)
            System.out.println("candidate pc to be deleted is: " +candidate_pc.getId());
            query.deleteComputer(Integer.parseInt(candidate_pc.getId()));
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
            Label compMD_lb = (Label) comp_settings_pane.lookup("#setting_comp_Modifier_lb");

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
                    String word_description = target_component_severity.getFirst().get(2); //haHAHA
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

            if (compST_lb.getText().equals("Active")){
                compST_lb.setStyle("-fx-background-color: #4ad66d;"); // green active
                compST_lb.setTextFill(Color.web("#f4f0bb"));
            } else {
                compST_lb.setStyle("-fx-background-color: #da2c38"); // red inactive
                compST_lb.setTextFill(Color.web("#f4f0bb"));
            }

            compST_lb.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    if (compST_lb.getText().equals("Active")){// disable pc when active
                        System.out.println("pc deactivated");
                        String deactivator = "UPDATE `computer` SET `status` = "+1+" WHERE `computer_number` = "+candidate_pc.getId();
                        query.updateData(deactivator);

                    } else { // activate pc when inactive
                        System.out.println("pc activated");
                        String activator = "UPDATE `computer` SET `status` = "+0+" WHERE `computer_number` = "+candidate_pc.getId();
                        query.updateData(activator);
                    }
                    comp_settings_pane.setVisible(false);
                }
            });


            compMD_lb.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    try {
                        query.resolveComputer(Integer.parseInt(candidate_pc.getId()));
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException(e);
                    }
                    comp_settings_pane.setVisible(false);
                }
            });


            compCD_lb.setText(condition); // computer condition get from query
        });
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
                } else if (btn_id.equals("comp_delete_Btn")) { // if delete is clicked
                    n.setOnMouseClicked(mouseEvent1 -> {
                        try {
                            compSettingsPane.setVisible(false);
                            deleteComp();
                        } catch (SQLException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (btn_id.equals("comp_movecomp_Btn")){ // if move computer is clicked
                    n.setOnMouseClicked(mouseEvent1 -> {
                        try {
                            compSettingsPane.setVisible(false);
                            // ROOM DIALOGUE
                            TextInputDialog textInputDialog1 = new TextInputDialog();
                            textInputDialog1.setTitle("Room Input");
                            textInputDialog1.getDialogPane().setContentText("Room Number");
                            Optional<String> room_result = textInputDialog1.showAndWait();

                            if (room_result.isPresent()){ // if ok is clicked in the room input
                                boolean proceed = false;
                                String selectedRoom = "";
                                ArrayList<String> rooms_list = query.getColumnData("room", "room_number");
                                for (String temp_room: rooms_list){
                                    if (temp_room.equals(room_result.get())){ // if that room exists then proceed
                                        proceed = true;
                                        selectedRoom = temp_room;
                                        break;
                                    }
                                }
                                if (proceed){
                                    TextInputDialog textInputDialog2 = new TextInputDialog();
                                    textInputDialog2.setTitle("Slot Input");
                                    textInputDialog2.getDialogPane().setContentText("Slot Number");
                                    Optional<String> slot_result = textInputDialog2.showAndWait();

                                    if (slot_result.isPresent() ){ // if ok is clicked in the slot
                                        int num_result_slot = Integer.parseInt(slot_result.get());

                                        if (num_result_slot <= 0  || num_result_slot > 40){ // check the slot format
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("INVALID SLOT");
                                            alert.setContentText("Invalid slot location");
                                            alert.show();
                                        } else {// if the format is okay then proceed
                                            ArrayList<ArrayList<String>> checker_list = query.readData("computer", "room_number = "+selectedRoom+" and room_slot = "+num_result_slot);
                                            if (checker_list.isEmpty()){ // if there is no computer in that slot
                                                System.out.println("CHEECKER LIOST: "+checker_list);
                                                // remove visually in this room
                                                int rowIndex = GridPane.getRowIndex(candidate_pc); // get the current pc location
                                                int colIndex = GridPane.getColumnIndex(candidate_pc);

                                                GridPane.setRowIndex(candidate_slot, rowIndex); // set the location of the label there
                                                GridPane.setColumnIndex(candidate_slot, colIndex);

                                                candidate_room.getChildren().remove(candidate_pc); // now visually remove current pc there
                                                candidate_room.getChildren().add(candidate_slot); // add the candidate slot


                                                // add visually in another room
                                                GridPane grid_selectedRoom = (GridPane) main.lookup("#"+selectedRoom); //select the input room
                                                Label label_selectedSlot = (Label) grid_selectedRoom.lookup("#"+slot_result.get()); // select the input label

                                                int newrowIndex = GridPane.getRowIndex(label_selectedSlot);
                                                int newcolIndex = GridPane.getColumnIndex(label_selectedSlot);
                                                GridPane.setRowIndex(candidate_pc, newrowIndex); // set the column of the pc on the column of the slot
                                                GridPane.setColumnIndex(candidate_pc, newcolIndex);

                                                grid_selectedRoom.getChildren().remove(label_selectedSlot); // remove the slot there
                                                grid_selectedRoom.getChildren().add(candidate_pc); // place the pc there

                                                setCompOnclick(candidate_pc, grid_selectedRoom, label_selectedSlot);
                                                query.moveComputer(Integer.parseInt(candidate_pc.getId()), Integer.parseInt(grid_selectedRoom.getId()), Integer.parseInt(label_selectedSlot.getText()));

                                            } else {
                                                Alert alertx = new Alert(Alert.AlertType.ERROR);
                                                alertx.setTitle("INVALID SLOT");
                                                alertx.setContentText("Invalid slot location");
                                                if (slot_result.get().equals(candidate_slot.getText()) && selectedRoom.equals(candidate_room.getId()) ){
                                                    alertx.setContentText("Error, the computer is already in this slot");
                                                } else {
                                                    alertx.setContentText("Invalid location");
                                                }
                                                alertx.show();
                                            }
                                        }
                                    }
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("INVALID ROOM");
                                    alert.setContentText("Error, invalid room");
                                    alert.show();
                                }
                            }
                        } catch (ClassNotFoundException | SQLException e) {
                            throw new RuntimeException(e);
                        } catch (NumberFormatException x){
                            Alert alerts = new Alert(Alert.AlertType.ERROR);
                            alerts.setTitle("INVALID SLOT NUMBER ");
                            alerts.setContentText("Error, invalid slot number format");
                            alerts.show();
                        }

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
    // room prompt
    public void setCurrent_room(String current_room) {
        this.current_room = current_room;
        System.out.println(current_room);
    }
    public void setCurrent_room_label(Label label_current_room){
        this.label_current_room = label_current_room;
    }
    // add room
    public void addRoom(TextField room_input, ArrayList<String> rooms_list){
        String txt_room = room_input.getText();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        try {
            // -- VALIDATOR -- (to check first before proceeding to the system
            int num_room = Integer.parseInt(txt_room);
            boolean proceed = true;
            for (String room: rooms_list){
                if (txt_room.equals(room)){
                    alert.setContentText("Room already exist");
                    alert.show();
                    proceed = false;
                    break;
                }
            } // -- END OF VALIDATOR --

            if (proceed){
                /* TODO backend  (add room) */
                query.createRoom(num_room);

                //refresh
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin_room_selection.fxml"));
                Parent root = null;
                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                AdminRoomSelection adminRoomSelection= fxmlLoader.getController();
                adminRoomSelection.setUser("admin"); // to be changed
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.show();
                Stage this_stage = (Stage)main.getScene().getWindow();
                this_stage.close();
            }

        } catch (NumberFormatException | NullPointerException e){ // if the entered room number has letters or is null
            alert.setContentText("Invalid Room Format");
            alert.show();
        }
    }
    private void addRoomClose(AnchorPane createRoomPane) {
        createRoomPane.setVisible(false);
    }


    // this just sets the action event when the menu items in the room settings are clicked
    public void addRoomAction(AnchorPane createRoomPane, ArrayList<String> rooms, TextField room_textfield) {
        for (Node n: createRoomPane.getChildren()){
            if (n instanceof Button){
                if (n.getId().equals("room_create_Btn")){
                    // create room
                    n.setOnMouseClicked(mouseEvent -> addRoom(room_textfield, rooms));
                } else if (n.getId().equals("room_close_Btn")){
                    // close room prompt
                    n.setOnMouseClicked(mouseEvent -> addRoomClose(createRoomPane));
                }
            }
        }
        createRoomPane.setVisible(false);

        this.createRoomPane = createRoomPane;
    }



    public void setMain(AnchorPane main) {
        this.main = main;
    }


    public void deleteRoomAction(int del_room_num) {
        System.out.println("Room deleted "+del_room_num);
        /* TODO backend (delete room)
            (delete all computers first with if their room number matches 'del_room_num
            (then finally delete the room that matches 'del_room_num')*/
        query.deleteRoom(del_room_num);
    }



}



