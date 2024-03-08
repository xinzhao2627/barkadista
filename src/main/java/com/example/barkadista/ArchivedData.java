package com.example.barkadista;

public class ArchivedData {
    int room_number;
    int computer_number;
    int report_number;
    String content_summary;
    String user_comment;
    String submittee;
    String date;
    public ArchivedData(int room_number, int computer_number, int report_number, String content_summary, String user_comment, String submittee, String date){
        this.room_number = room_number;
        this.computer_number = computer_number;
        this.report_number = report_number;
        this.content_summary = content_summary;
        this.user_comment = user_comment;
        this.submittee = submittee;
        this.date = date;
    }

    public int getRoom_number() {
        return room_number;
    }

    public int getComputer_number() {
        return computer_number;
    }

    public int getReport_number() {
        return report_number;
    }

    public String getContent_summary() {
        return content_summary;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public String getDate() {
        return date;
    }

    public String getSubmittee() {
        return submittee;
    }
}

