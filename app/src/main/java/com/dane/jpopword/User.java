package com.dane.jpopword;

import java.util.ArrayList;

public class User {
  String uIdFirebase;
  String emailFirebase;
  String nickname;
  String lastLoginDate;

  int quizTicket;
  ArrayList<String> badges; // Song - songSaveFormat()

  ArrayList<String> favorites; // 좋아요 누른 노래들 Song - songSaveFormat()
  ArrayList<String> voca; // 단어장에 등록한 단어들 Word - wordSaveFormat()
}
