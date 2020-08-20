package com.dane.jpopword.model;

public class newWord {
    private int id;
    private String wordJap;
    private String wordJapPron;
    private String wordKor;
    private String wordKorPron;
    private String sentenceJap;
    private String sentenceKor;
    private String sentencePron;

    public newWord(int id, String wordJap, String wordJapPron, String wordKor, String wordKorPron, String sentenceJap, String sentenceKor, String sentencePron) {
        this.id = id;
        this.wordJap = wordJap;
        this.wordJapPron = wordJapPron;
        this.wordKor = wordKor;
        this.wordKorPron = wordKorPron;
        this.sentenceJap = sentenceJap;
        this.sentenceKor = sentenceKor;
        this.sentencePron = sentencePron;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordJap() {
        return wordJap;
    }

    public void setWordJap(String wordJap) {
        this.wordJap = wordJap;
    }

    public String getWordJapPron() {
        return wordJapPron;
    }

    public void setWordJapPron(String wordJapPron) {
        this.wordJapPron = wordJapPron;
    }

    public String getWordKor() {
        return wordKor;
    }

    public void setWordKor(String wordKor) {
        this.wordKor = wordKor;
    }

    public String getWordKorPron() {
        return wordKorPron;
    }

    public void setWordKorPron(String wordKorPron) {
        this.wordKorPron = wordKorPron;
    }

    public String getSentenceJap() {
        return sentenceJap;
    }

    public void setSentenceJap(String sentenceJap) {
        this.sentenceJap = sentenceJap;
    }

    public String getSentenceKor() {
        return sentenceKor;
    }

    public void setSentenceKor(String sentenceKor) {
        this.sentenceKor = sentenceKor;
    }

    public String getSentencePron() {
        return sentencePron;
    }

    public void setSentencePron(String sentencePron) {
        this.sentencePron = sentencePron;
    }
}
