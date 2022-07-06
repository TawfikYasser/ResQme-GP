package com.example.resqme.model;

public class QuestionReply {
    String questionReplyID, questionReplyQuestionID, questionReplySPID, questionReplyText;

    public QuestionReply(String questionReplyID, String questionReplyQuestionID, String questionReplySPID, String questionReplyText) {
        this.questionReplyID = questionReplyID;
        this.questionReplyQuestionID = questionReplyQuestionID;
        this.questionReplySPID = questionReplySPID;
        this.questionReplyText = questionReplyText;
    }

    public QuestionReply() {
    }

    public String getQuestionReplyID() {
        return questionReplyID;
    }

    public void setQuestionReplyID(String questionReplyID) {
        this.questionReplyID = questionReplyID;
    }

    public String getQuestionReplyQuestionID() {
        return questionReplyQuestionID;
    }

    public void setQuestionReplyQuestionID(String questionReplyQuestionID) {
        this.questionReplyQuestionID = questionReplyQuestionID;
    }

    public String getQuestionReplySPID() {
        return questionReplySPID;
    }

    public void setQuestionReplySPID(String questionReplySPID) {
        this.questionReplySPID = questionReplySPID;
    }

    public String getQuestionReplyText() {
        return questionReplyText;
    }

    public void setQuestionReplyText(String questionReplyText) {
        this.questionReplyText = questionReplyText;
    }
}
