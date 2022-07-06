package com.example.resqme.model;

public class Question {
    String questionID, questionCustomerID, questionText;

    public Question(String questionID, String questionCustomerID, String questionText) {
        this.questionID = questionID;
        this.questionCustomerID = questionCustomerID;
        this.questionText = questionText;
    }

    public Question() {
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    public String getQuestionCustomerID() {
        return questionCustomerID;
    }

    public void setQuestionCustomerID(String questionCustomerID) {
        this.questionCustomerID = questionCustomerID;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
