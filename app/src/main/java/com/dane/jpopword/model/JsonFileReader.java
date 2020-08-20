package com.dane.jpopword.model;

import android.content.Context;
import android.util.Log;

import com.dane.jpopword.Category;
import com.dane.jpopword.Song;
import com.dane.jpopword.SplashActivity;
import com.dane.jpopword.WordCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonFileReader {
    /* [수정 필요] 다 이렇게 고치셈
    for(int loop=0; loop<v.size();loop++)
    위처럼 코딩을 하는 습관은 좋지 않습니다. 매번 반복하면서 v.size() 메소드를 호출하기 때문입니다.
    int vSize=v.size();
    for(int loop=0; loop<vSize; loop++)
    이렇게 하면 필요없는 size() 메소드 반복 호출이 없어지므로 더 빠르게 처리됩니다.
    */

    public static ArrayList<WordCard> makeWordsObjectFromJsonFile(Context context, String jsonFileName){
        Gson gson2 = new Gson();
        Type type2 = new TypeToken<ArrayList<WordCard>>() { // 타입 추론을 위한 부분
        }.getType();
        ArrayList<WordCard> words = gson2.fromJson(getJsonFromAssets(context, jsonFileName), type2);

        SplashActivity.wordsAll = words;
        return words;
    }


    public static ArrayList<Song> makeObjectFromJsonFile(Context context, String jsonFileName1, ArrayList<WordCard> words){
        // [1] player.json을 통해 Player 객체들을 생성함. playerlist에 담음.
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Song>>() { // 커스텀 클래스라 타입을 명확히 말해줘야해.(타입추론)
        }.getType();
        ArrayList<Song> songs = gson.fromJson(getJsonFromAssets(context, jsonFileName1), type);
        // 와 존나 어려웠다. ArrayList<>와 java.util.arraylist ArrayList<>가 다르고, Hashcode라는 개념까지 나오는 문제였는데 여튼 해결은 함. 물론 이해는 안됨.

        // 모든 노래, 모든 단어가 각각 for문 돌면서 songId가 같으면(!!!) word에 들어간다. (songId가 기준이니 google 시트에서 칼럼명 함부로 바꾸면 안됨)
        for(int k=0;k<songs.size();k++){
            songs.get(k).setWords(new ArrayList<WordCard>());
            for (int i=0;i<words.size();i++){
                if(songs.get(k).getCover()==0){ // 만약 커버곡이 없으면
                    // songId랑 word의 songId랑 비교해서 넣고
                    if(songs.get(k).getSongId() == words.get(i).songId){
                        songs.get(k).addWord(words.get(i));
                        //Log.d("DATACONTROL", "들어감?" + songs.get(k).titleKor +"에 "+words.get(i).wordKor+"가 들어감");
                    }
                } else{ // 만약 커버곡이 있으면 (cover!=0)
                    // cover랑 word의 songId랑 비교해서 넣고
                    if(songs.get(k).getCover() == words.get(i).songId){
                        songs.get(k).addWord(words.get(i));
                    }
                }
            }
        }

        // Song 안에 word들이 제대로 들어갔는지 확인.
        //Log.d("DATACONTROL", ""+songs.get(0).words.get(0).wordKor);
        SplashActivity.songsAll = songs;
        return songs;
    }

    // JSON 파일에서 내용(String) 읽어오는 함수
    static String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return jsonString;
    }


    // assets 폴더의 JSON 파일로부터 카테고리 리스트를 리턴받는 함수.
    // 각각의 카테고리 리스트는 ArrayList<Class>임.
    static ArrayList<ArrayList<Song>> getCategoryListFromJsonFile(
        //Context context, String songsJsonFileName, String wordsJsonFileName, ArrayList<String> catNames) {
        Context context, ArrayList<Song> songs, ArrayList<WordCard> words, ArrayList<String> catNames) {

        long startTime = System.currentTimeMillis();

        // 카테고리 별로 담아놓을 리스트를 만들고
        ArrayList<ArrayList<Song>> catSongLists = new ArrayList<ArrayList<Song>>(catNames.size());

        // 모든 songs를 돌면서 각 클래스의 categoryName 에 맞춰 다른 categoryList(result(i))에 담아준다. 그리고 리턴.
        int catSize = catNames.size();
        int songsSize = songs.size();
        for(int t=0;t<catSize;t++){
            for(int i=0;i<songsSize;i++){
                // song의 catName이 분류하려는 catName과 같으면 => 리스트에 추가한다.
                // 카테고리가 없는 경우는 없어. 서로 다른 카테고리에 노래가 중복으로 들어가는 경우는 있을 수 있음(*중요*) : 아직 없긴 하다만
               if(songs.get(i).getCatName().equals(catNames.get(t))){
                   catSongLists.add(new ArrayList<Song>()); // 얘를 추가함으로써 null pointer exception을 피함.
                   catSongLists.get(t).add(songs.get(i));
                   // 그런데 빈 카테고리가 생기는 문제가 있는데. 그건 밑에서 따로 for문 돌면서 없애버릴게
                   //Log.d("DATACONTROL", "category "+t+"에 "+ songs.get(i).getTitleKor()+"가 들어감");
               }
            }
        }

        // 중복 제거하는 코드
        for (int i=0;i<catSongLists.size();i++){
            int deleteCount=0;
            if(catSongLists.get(i).isEmpty() || catSongLists.get(i).get(0)==null ||catSongLists.get(i).size()==0){
                catSongLists.remove(i);
                i--; // remove() 지우고 뒤에 아이템들을 앞으로 한칸씩 땡김
                deleteCount++;
                //Log.d("DATACONTROL", "category "+i+"는 아무것도 없으니 지운다("+deleteCount+")");
            }
        }

        long flagTime3 = System.currentTimeMillis();
        Log.d("DATACONTROL", "TIME CHECK : " + (flagTime3-startTime));

        // 이제 각 category들을 돌며 words를 song마다 넣어주면 JSON 데이터 객체화 처리 완성
        int catSongListsSize = catSongLists.size();
        int wordsSize = words.size();
        for(int c=0; c<catSongListsSize; c++){  // c개의 cat가 있고
            for(int s=0; s<catSongLists.get(c).size(); s++){ // 각각의 cat은 각각 s개의 song이 있고
                catSongLists.get(c).get(s).setWords(new ArrayList<WordCard>());
                for(int w=0;w<wordsSize;w++){ // words는 별도로 w개가 있으니
                    if(catSongLists.get(c).get(s).getCover()==0){ // 만약 커버곡이 없으면
                        // songId랑 word의 songId랑 비교해서 넣고
                        if(catSongLists.get(c).get(s).getSongId() == words.get(w).songId){
                            catSongLists.get(c).get(s).addWord(words.get(w));
                            //Log.d("DATACONTROL", "들어감?" + songs.get(k).titleKor +"에 "+words.get(i).wordKor+"가 들어감");
                        }
                    } else{ // 만약 커버곡이 있으면 (cover!=0)
                        // cover랑 word의 songId랑 비교해서 넣고
                        if(catSongLists.get(c).get(s).getCover() == words.get(w).songId){
                            catSongLists.get(c).get(s).addWord(words.get(w));
                        }
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        Log.d("DATACONTROL", "TIME CHECK : " + (endTime-flagTime3));

        return catSongLists;
    }
    //


    public static ArrayList<Category> makeCategoriesFromJsonFile(Context context, String jsonFileName, ArrayList<Song> songs){
        // [1] player.json을 통해 Player 객체들을 생성함. playerlist에 담음.
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Category>>() { // 커스텀 클래스라 타입을 명확히 말해줘야해.(타입추론)
        }.getType();
        ArrayList<Category> categories = gson.fromJson(getJsonFromAssets(context, jsonFileName), type);

        // 모든 노래, 모든 단어가 각각 for문 돌면서 songId가 같으면(!!!) word에 들어간다. songId가 기준이면 google 시트에서 이 칼럼 함부로 바꾸면 안되겠는걸.
        for(int k=0;k<categories.size();k++){
            categories.get(k).setSongs(new ArrayList<Song>());
            for (int i=0;i<songs.size();i++){
                // songId랑 word의 songId랑 비교해서 넣고
                if(categories.get(k).getName().equals(songs.get(i).getCatName())){
                    categories.get(k).addSong(songs.get(i));
                    Log.d("DATACONTROL 333", "들어감?" + categories.get(k).getName() +"에 "+songs.get(i).getTitleKor()+"가 들어감");
                }
            }
        }

        // Song 안에 word들이 제대로 들어갔는지 확인.
        Log.d("DATACONTROL 44", ""+categories.get(0).getSongs().get(0).getWords().get(0).wordSaveFormat());

        SplashActivity.categoryAll = categories;
        return categories;
    }

    public static ArrayList<Object> makeAllObjectFromJsonFile(Context context, String jsonFileNameCat, String jsonFileNameSong, String jsonFileNameWord){
        ArrayList<Object> temp = new ArrayList<>() ;
        ArrayList<Category> cats = new ArrayList<>() ;
        ArrayList<Song> songs = new ArrayList<>() ;
        ArrayList<WordCard> words = new ArrayList<>() ;
        // ====== //

        // ====== //
        temp.add(cats);
        temp.add(songs);
        temp.add(words);
        return temp;
    }


    // =============== // =============== // =============== // =============== // =============== // =============== //
    //  자료구조 변경 중

    public static ArrayList<newCategory> newAllCategories(Context context, String jsonFileName){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<newCategory>>() { // 타입 추론을 위한 부분
        }.getType();
        ArrayList<newCategory> newCategories = gson.fromJson(getJsonFromAssets(context, jsonFileName), type);

        //SplashActivity.wordsAll = words;
        return newCategories;
    }

    public static ArrayList<newSong> newAllSongs(Context context, String jsonFileName){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<newSong>>() { // 타입 추론을 위한 부분
        }.getType();
        ArrayList<newSong> newSongs = gson.fromJson(getJsonFromAssets(context, jsonFileName), type);

        //SplashActivity.wordsAll = words;
        return newSongs;
    }

    public static ArrayList<newWord> newAllWords(Context context, String jsonFileName){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<newWord>>() { // 타입 추론을 위한 부분
        }.getType();
        ArrayList<newWord> newWords = gson.fromJson(getJsonFromAssets(context, jsonFileName), type);

        //SplashActivity.wordsAll = words;
        return newWords;
    }





//
} // end of class

