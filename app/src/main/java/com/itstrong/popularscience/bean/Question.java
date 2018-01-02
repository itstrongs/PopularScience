package com.itstrong.popularscience.bean;

import java.util.List;

/**
 * Created by itstrong on 2016/6/21.
 */
public class Question {

    private String answer;  //题目答案

    private String content;  //题目内容

    private String id;  //题目id

    private List<String> option;  //题目选项

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOption() {
        return option;
    }

    public void setOption(List<String> option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "Question{" +
                "answer='" + answer + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", option=" + option +
                '}';
    }
}
