package com.br.cocus;

public class FileInformation {
    private String content;
    private Integer lineNumber;
    private String fileName;
    private String frequentLetter;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFrequentLetter() {
        return frequentLetter;
    }

    public void setFrequentLetter(String frequentLetter) {
        this.frequentLetter = frequentLetter;
    }

    @Override
    public String toString() {
        return      "Random Line: " + content
                + "\nLine Number: " + lineNumber
                + "\nFile Name: " + fileName
                + "\nThe Letter with occurs most often in this Line: " + frequentLetter;
    }
}
