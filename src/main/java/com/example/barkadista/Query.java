package com.example.barkadista;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
 * UPDATE DETAILS:
 * - computer condition changed from int to string (same as component_severity)
 * - adjust queries based on changes in computer condition
 */

public class Query{

    private Connection con;
    private String[] components = {"mouse", "keyboard", "monitor", "cables", "internet connectivity", "operating system"};

    public String[] getComponents() {
        return this.components;
    }

    //establish connection to sql
    public void connectSQL() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        //Credentials
        String url = "jdbc:mysql://localhost:3306/barkadista_db";
        String username = "root";
        String password = "";

        //Establish the connection
        this.con = DriverManager.getConnection(url, username, password);

    }

    //parang readData() pero mismong query na ilalagay mo kung gusto mo ng specific na query
    public ArrayList<ArrayList<String>> readData2(String query) throws ClassNotFoundException, SQLException {

        ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();

        try{

            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs = ((java.sql.Statement) st).executeQuery(query);

            //Store data to array
            while (rs.next()) {
                arr.add(new ArrayList<String>());

                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    arr.get(arr.size() - 1).add(rs.getString(i));
                }
            }

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return arr;
    }

    //get data and returns 2d array list
    public ArrayList<ArrayList<String>> readData(String table) throws ClassNotFoundException, SQLException {

        ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();

        try{

            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            String query = "SELECT * FROM `" + table + "`";

            ResultSet rs = ((java.sql.Statement) st).executeQuery(query);


            //Store data to array
            while (rs.next()) {
                arr.add(new ArrayList<String>());

                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    arr.get(arr.size() - 1).add(rs.getString(i));
                }
            }

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return arr;
    }

    public ArrayList<ArrayList<String>> readData(String table, String condition) throws ClassNotFoundException, SQLException {
        //Overload method of readData with the addition of condition parameter

        ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();

        try{

            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            String query = "SELECT * FROM `" + table + "` WHERE " + condition;

            ResultSet rs = ((java.sql.Statement) st).executeQuery(query);

            //Store data to array
            while (rs.next()) {
                arr.add(new ArrayList<String>());

                for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    arr.get(arr.size() - 1).add(rs.getString(i));
                }
            }
            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return arr;
    }

    //update database (insert/update/alter/drop/... command)
    public void updateData(String sql) {
        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            st.executeUpdate(sql);
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }
    }

    //delete data from report table only
    public void deleteReport(int report_number) {
        /* This method will not just delete the data, but also archived it.
         *
         * The process of this method:
         * 	1) get the data that will be deleted, then store it in the array list (archive).
         * 	2) insert the archived data in the archive table in database
         * 	3) delete the data
         */

        try{
            connectSQL();

            ArrayList<String> archive = new ArrayList<String>();

            String query = "DELETE FROM `report` WHERE `report_number` = " + report_number;

            java.sql.Statement st = this.con.createStatement();


            //Get the data by storing it to array list before deleting the row

            ResultSet rs = ((java.sql.Statement) st).executeQuery("SELECT * FROM `report` WHERE `report_number` = " + report_number);

            while (rs.next()) {//Store data to array

                for(int i = 1; i <= 7; i++) {
                    archive.add(rs.getString(i));
                }
            }

            //Insert the retrieved data to archive table

            //format syntax of INSERT command
            String new_sql = "INSERT INTO `archive_report`(`room_number`, `computer_number`, `report_number`, `content_summary`, `content`, `submitter`, `report_date`) "
                    + "VALUES (" + archive.get(0) + ", " + archive.get(1) + ", " + archive.get(2) + ", '" + archive.get(3) + "', '" + archive.get(4) + "', '"
                    + archive.get(5) + "', '" + archive.get(6) + "')";

            updateData(new_sql);

            // Delete the row

            updateData(query);

            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }


    }

    //get data from specific column
    public ArrayList<String> getColumnData(String table, String column) throws ClassNotFoundException, SQLException {

        ArrayList<String> arr = new ArrayList<String>();

        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs = ((java.sql.Statement) st).executeQuery("SELECT `" + column + "` FROM `" + table + "`");

            //Store data to array
            while (rs.next()) {
                arr.add(rs.getString(1));
            }

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }
        return arr;
    }

    public ArrayList<String> getColumnData(String table, String column, String condition) throws ClassNotFoundException, SQLException {
        /*Overload method of getColumnData
         */

        ArrayList<String> arr = new ArrayList<String>();

        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs = ((java.sql.Statement) st).executeQuery("SELECT `" + column + "` FROM `" + table + "` WHERE " + condition);

            //Store data to array
            while (rs.next()) {
                arr.add(rs.getString(column));
            }

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }
        return arr;
    }

    //return the computer id of a computer (optional)
    public int getComputerID(int comp_number) {

        int comp_id = 0;

        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs1 = ((java.sql.Statement) st).executeQuery("SELECT * FROM computer WHERE comp_number = " + comp_number);

            rs1.next();

            //format the computer id to 6 digits

            int roomNum = rs1.getInt("room_num");
            int compNum = rs1.getInt("comp_number");

            roomNum = roomNum * 1000;

            comp_id = roomNum + compNum;

            rs1.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return comp_id;
    }

    //return the total number of reports
    public int getTotalReport() {

        int total_report = 0;

        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs = ((java.sql.Statement) st).executeQuery("SELECT COUNT(*) as report_count FROM `report`");
            rs.next();

            total_report = rs.getInt(1);

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return total_report;
    }

    //return the total reports in a room
    public int getRoomReport(int room_num) {


        int total_report = 0;

        try{
            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            ResultSet rs = ((java.sql.Statement) st).executeQuery("SELECT COUNT(*) as report_count FROM `report` WHERE room_number = " + room_num);
            rs.next();

            total_report = rs.getInt(1);

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return total_report;

    }

    //update and return condition value of a component
    public float updateComponentSeverity(int computer_number, String component) throws ClassNotFoundException, SQLException {

        ArrayList<Float> component_severity = new ArrayList<Float>(2);//holder of severity value and condition value

        String condition = "computer_number = " + computer_number + " AND content_summary LIKE '%" + component + "%'";

        ArrayList<String> component_condition = getColumnData("report", "content_summary", condition);

        if(component_condition.isEmpty()) {

            component_severity.add((float) 0);
            component_severity.add((float) 0);

        }
        else {
            //setting component severity

            float severity_value = 0f;

            for(int i = 0; i < component_condition.size(); i++) {
                String[] splitStr = component_condition.get(i).split("_");
                String s = splitStr[splitStr.length - 1].toLowerCase();

                int severity = 0;

                switch(s) {
                    case "active":
                    case "usable":
                        severity = 0;
                        break;

                    case "minor issue":
                        severity = 1;
                        break;

                    case "major issue":
                        severity = 2;
                        break;

                    case "inactive":
                    case "unusable":
                        severity = 3;
                        break;
                }
                severity_value += severity;
            }

            //average severity value of component

            component_severity.add((float) (severity_value /= component_condition.size()));


            //calculating component condition using the coefficient

            //components coefficient
            int mouse = 5;
            int keyboard = 8;
            int monitor	= 10;
            int cables = 7;
            int os = 10;
            int internet = 6;

            switch(component.toLowerCase()){
                case "mouse":
                    severity_value = (mouse * mouse) * severity_value;
                    break;

                case "keyboard":
                    severity_value = (keyboard * keyboard) * severity_value;
                    break;

                case "monitor":
                    severity_value = (monitor * monitor) * severity_value;
                    break;

                case "cables":
                case "cable":
                    severity_value = (cables * cables) * severity_value;
                    break;

                case "operating system":
                    severity_value = (os * os) * severity_value;
                    break;

                case "internet connectivity":
                    severity_value = (internet * internet) * severity_value;
                    break;
            }

            component_severity.add((float) severity_value);
        }

        //update severity of components

        String word_condition = "";

        //set component_severity into word_condition

        //ranges 0 -1 (active/usable)
        if(component_severity.get(0) >= 0 && component_severity.get(0) < 1) {
            word_condition = "NO ISSUE";
        }
        //ranges 1 - 2 (minor issue)
        if(component_severity.get(0) >= 1 && component_severity.get(0) < 2) {
            word_condition = "MINOR ISSUE";
        }
        //ranges 2 -3 (major)
        if(component_severity.get(0) >= 2 && component_severity.get(0) < 3) {
            word_condition = "MAJOR ISSUE";
        }
        //3 above (unusable/inactive)
        if(component_severity.get(0) >= 3) {
            word_condition = "UNUSABLE";
        }

        String query = "UPDATE `components` SET `component_severity`= '" + word_condition
                + "' WHERE computer_number = " + computer_number + " AND component_name = '" + component + "'";

        updateData(query);

        return component_severity.get(1);
    }

    //return number of data
    public int getCountData(String table, String column, String condition) {

        int count = 0;

        try{

            connectSQL();

            java.sql.Statement st = this.con.createStatement();

            //SELECT COUNT(column_name) FROM table_name WHERE condition;

            String query = "SELECT COUNT(" + column + ") FROM " + table + " WHERE " + condition;

            ResultSet rs = ((java.sql.Statement) st).executeQuery(query);
            rs.next();
            //Store data to array
            count = rs.getInt(1);

            rs.close();
            con.close();

        }catch(Exception e) {
            System.out.println(e);
        }

        return count;

    }

    //updates the data of the room
    public void updateRoomData() throws ClassNotFoundException, SQLException {
        ArrayList<String> room_number = getColumnData("room", "room_number");

        for(String s : room_number) {

            int active_pc = getCountData("computer", "`status`", ("`status` = 0 AND room_number = " + Integer.parseInt(s)));
            int inactive_pc = getCountData("computer", "`status`", ("`status` = 1 AND room_number = " + Integer.parseInt(s)));
            int minor_issues = getCountData("computer", "`condition`", ("`condition` = 'WITH MINOR ISSUE' AND room_number = " + Integer.parseInt(s)));
            int major_issues = getCountData("computer", "`condition`", ("`condition` = 'WITH MAJOR ISSUE' AND room_number = " + Integer.parseInt(s)));
            int total_pc = getCountData("computer", "`computer_number`", ("room_number = " + Integer.parseInt(s)));
            int reports = getRoomReport(Integer.parseInt(s));

            String query = "UPDATE `room` SET `active_pc`= " + active_pc + ", `inactive_pc`= " + inactive_pc + ", `minor_issues`= " + minor_issues
                    + ", `major_issues`= " + major_issues + ", `reports`= " + reports + ", `total_pc`= " + total_pc + " WHERE room_number = " + Integer.parseInt(s);
            updateData(query);
        }

    }

    //return data of a room
    public ArrayList<Integer> getRoomData(int room_number){
        ArrayList<Integer> arr = new ArrayList<Integer>();

        arr.add(getCountData("computer", "`status`", ("`status` = 0 AND room_number = " + room_number)));
        arr.add(getCountData("computer", "`status`", ("`status` = 1 AND room_number = " + room_number)));
        arr.add(getCountData("computer", "`condition`", ("`condition` = 'WITH MINOR ISSUE' AND room_number = " + room_number)));
        arr.add(getCountData("computer", "`condition`", ("`condition` = 'WITH MAJOR ISUUE' AND room_number = " + room_number)));
        arr.add(getRoomReport(room_number));

        return arr;
    }

    //resolves the computer
    public void resolveComputer(int computer_number) throws ClassNotFoundException, SQLException {

        String query = "UPDATE `computer` SET `status`= 0,`condition`= 'GOOD CONDITION' WHERE computer_number = " + computer_number;

        updateData(query);

        //resolve components

        String query2 = "UPDATE `components` SET `component_severity`= 'NO ISSUE' WHERE computer_number = " + computer_number;
        updateData(query2);

        //delete related reports
        String condition = "computer_number = " + computer_number;
        ArrayList<String> report_numbers = getColumnData("report", "report_number", condition);

        for(String i: report_numbers){
            deleteReport(Integer.parseInt(i));
        }

        //update room data
        updateRoomData();
        System.out.println("computer successfully resolve");
    }

    //disable the computer
    public void disableComputer(int computer_number) throws NumberFormatException, ClassNotFoundException, SQLException {

        //set computer as inactive and unusable
        String query = "UPDATE `computer` SET `status`= 1, `condition`= 'BAD CONDITION' WHERE computer_number = " + computer_number;

        updateData(query);

        //set components as unusable
        String query2 = "UPDATE `components` SET `component_severity`= 'UNUSABLE' WHERE computer_number = " + computer_number;
        updateData(query2);

        //update room data
        updateRoomData();


        System.out.println("computer temporarily closed");

    }

    //rename room number (optional)
    public void renameRoom(int old_room_number, int new_room_number) {

        //update room number
        String query1 = "UPDATE `room` SET room_number = " + new_room_number + " WHERE `room_number` = " + old_room_number;

        updateData(query1);

        //update room number in reports

        String query2 = "UPDATE `report` SET room_number = " + new_room_number + " WHERE `room_number` = " + old_room_number;

        updateData(query2);
    }

    //register new students
    public boolean registerStudent(String student_id) throws ClassNotFoundException, SQLException {
        //check if student id is valid
        //criteria 1: numbers before the hyphen (-) must be 4 digits
        //criteria 2: number after the hyphen (-) must be 6 digits AND starts with 1 or 2

        try{
            //first, check if student id format is correct
            if(student_id.contains("-") && student_id.length() == 11) {
                //split student id
                String str = student_id;
                String[] arrOfStr = str.split("-");

                String id_leftPart = arrOfStr[0];//four digit part
                String id_rightPart = arrOfStr[1];//six digit part

                String id = (id_leftPart + id_rightPart);
                int i = Integer.parseInt(id);
                //if valid, then proceed
                if(id_leftPart.length() == 4 && (id_rightPart.length() == 6 && (id_rightPart.startsWith("1") || id_rightPart.startsWith("2")))) {
                    System.out.println("ID is valid");

                    ArrayList<String> students = getColumnData("student", "student_id");

                    if(!students.contains(student_id)) {//insert student id if new
                        String query = "INSERT INTO `student`(`student_ID`) VALUES ('" + student_id + "')";
                        updateData(query);
                    }
                    return true;
                    //(else part) do noting
                }
                // (else part) do nothing
            }
            //(else part) do nothing
            return false;
        }catch(Exception e){
            //invalid input
            return false;
        }
    }

    //create a room
    public void createRoom(int room_num) {
        try{
            ArrayList <String> arr = getColumnData("room", "room_number");

            if(arr.contains(Integer.toString(room_num))) {
                System.out.println("ROOM " + room_num + " ALREADY EXIST");
            }else {

                //format syntax for CREATE TABLE
                String query = "INSERT INTO `room`(`room_number`, `total_pc`, `active_pc`, `inactive_pc`) VALUES (" + room_num + ", " + 0 + ", " + 0 + ", " + 0 + ")";
                updateData(query);

                System.out.println("ROOM " + room_num + " SUCCESSFULLY CREATED");
            }

        }catch(Exception e) {
            System.out.println(e);
        }
    }

    //delete room
    public void deleteRoom(int room_num) {
        try{
            ArrayList<String> arr = getColumnData("room", "room_number");

            if(!arr.contains(Integer.toString(room_num))) {
                System.out.println("ROOM " + room_num + " DO NOT EXIST");
            }else {
                //two queries for updating two tables

                //query for deleting the room data in room table
                String query1 = "DELETE FROM `room` WHERE `room_number` = " + room_num;

                //query for deleting all the pc within the room
                updateData(query1);
                System.out.println("ROOM " + room_num + " SUCCESSFULLY DELETED");
            }

        }catch(Exception e) {
            System.out.println(e);
        }
    }
    
    //submits report
    public void addReport(int room_number, int computer_number, String content_summary, String content, String submitter, String report_date) throws ClassNotFoundException, SQLException {

        String cond = "room_number = " + room_number;

        ArrayList<String> arr1 = getColumnData("room", "room_number");
        ArrayList<String> arr2 = getColumnData("computer", "computer_number", cond);;


        //condition if room do not exist, cannot input computer
        if(!arr1.contains(Integer.toString(room_number)) || !arr2.contains(Integer.toString(computer_number))) {
            System.out.println("ROOM DOES NOT EXIST or ROOM NUMBER DOESNT EXIST");
        }else {
            //query for inserting this report to the report table

            String query1 = "INSERT INTO `report`(`room_number`, `computer_number`, `content_summary`, `content`, `submitter`, `report_date`) "
                    + "VALUES (" + room_number + ", " + computer_number + ", '" + content_summary + "', '" + content + "', '" + submitter + "', '" + report_date + "')";

            updateData(query1);
            System.out.println("REPORT SUCCESSFULLY SUBMITTED");



            //query for updating component table/ computer condition & status

            //split content_summary
            String str = content_summary;
            String[] arrOfStr = str.split("_");

            String computer_status = arrOfStr[0].toString();

            String[] components = getComponents();
            float computer_condition = 0f;

            //calculate the COMPUTER condition

            for(int i = 0; i < components.length; i++) {
                computer_condition += updateComponentSeverity(computer_number, components[i]);
            } computer_condition /= components.length;


            //setting the status of the COMPUTER

            int status = 0;

            switch(computer_status.toLowerCase()) {
                case "active":
                    status = 0;
                    break;
                case "inactive":
                    status = 1;
                    break;
            }

            //setting the condition of the computer (STRING)

            String condition = "";	//for converting condition into string

            if(computer_condition == 0) {
                //usable
                condition = "GOOD CONDITION";
            }
            if(computer_condition > 0 && computer_condition <= 25) {
                //minor issues
                condition = "WITH MINOR ISSUE";
            }
            if(computer_condition > 25 && computer_condition < 50) {
                //major issues
                condition = "WITH MAJOR ISSUE";
            }
            if(computer_condition >= 50) {
                //unusable
                condition = "BAD CONDITION";
            }


            String query3 = "UPDATE `computer` SET `status`= " + status + ", `condition`= '" + condition + "' WHERE computer_number = " + computer_number;
            updateData(query3);
            System.out.println("COMPUTER SUCCESSFULLY UPDATED");

            //query for updating stats in room

            updateRoomData();

        }
    }

    //add a computer
    public void addComputer(int room_number, int room_slot) throws ClassNotFoundException, SQLException {


        String condition = "room_number = " + room_number;

        ArrayList<String> arr1 = getColumnData("room", "room_number");
        ArrayList<String> arr2 = getColumnData("computer", "room_slot", condition);;


        //condition if room do not exist, cannot input computer
        if(!arr1.contains(Integer.toString(room_number)) || arr2.contains(Integer.toString(room_slot))) {
            System.out.println("ROOM DOES NOT EXIST or SLOT ALREADY OCCUPIED");
        }else {

            //three queries for updating two tables

            //query for inserting pc
            String query1 = "INSERT INTO `computer`(`room_number`, `room_slot`) "
                    + "VALUES (" + room_number + ", " + room_slot + ")";

            updateData(query1);

            //query for adding components
            String condition2 = "room_slot = " + room_slot + " and room_number = " + room_number; // typo added space and condition added = 'and room_number = "+room_number'
            ArrayList<String> arr3 = getColumnData("computer", "computer_number", condition2);

            for(int i = 0; i < components.length; i++) {
                String query2 = "INSERT INTO `components`(`computer_number`, `component_name`, `component_severity`)"
                        + " VALUES (" + arr3.get(0) + ", '" + components[i] + "', 'NO ISSUE'" + ")";
                updateData(query2);
            }

            //query for updating the number of pc in a room
            updateRoomData();

            System.out.println("computer successfully added");
        }

    }

    //delete a computer and archiving reports related to the computer
    public void deleteComputer(int computer_number) throws ClassNotFoundException, SQLException {

        String condition = "computer_number = " + computer_number;
        ArrayList<String> arr = getColumnData("computer", "room_number", condition);

        if(arr.size()==0) {
            System.out.println("COMPUTER " + computer_number + " DO NOT EXIST.");
        }else{

            //archive report before deleting computer

            ArrayList<String> report_number = getColumnData("report", "report_number", ("computer_number = " + computer_number));

            for(String str : report_number) {
                deleteReport(Integer.parseInt(str));
            }

            //two queries for updating two tables

            //query for deleting computer in the table
            String query1 = "DELETE FROM `computer` WHERE `computer_number` = " + computer_number;

            //query for updating the number of pc

            updateData(query1);

            //update room data

            updateRoomData();
            System.out.println("COMPUTER SUCCESSFULLY DELETED");
        }
    }

    //move a computer into different slot (optional)
    public void moveComputer(int computer_number, int room_number, int room_slot) throws ClassNotFoundException, SQLException {
        //will remove condition statement depending on the process in UI

        ArrayList<String> arr1 = getColumnData("computer", "computer_number");
        ArrayList<String> arr2 = getColumnData("room", "room_number");

        if(!arr1.contains(Integer.toString(computer_number)) || !arr2.contains(Integer.toString(room_number))) {
            System.out.println("COMPUTER/ROOM DOESN'T EXIST");
        }else {

            int old_room_number = Integer.parseInt(getColumnData("computer", "room_number", ("computer_number = " + computer_number)).get(0));//get the old room number of the computer

            String condition1 = "computer_number = " + computer_number;

            //query for updating room slot of the computer
            String query = "UPDATE `computer` SET `room_number` = " + room_number + ", `room_slot`= " + room_slot + " WHERE " + condition1;
            updateData(query);

            //update room in reports
            String query2 = "UPDATE `report` SET room_number = " + room_number + " WHERE `room_number` = " + old_room_number;
            updateData(query2);

            //update room data
            updateRoomData();
            System.out.println("ROOM SUCCESSFULLY MOVED TO ANOTHER ROOM");

        }
    }

}