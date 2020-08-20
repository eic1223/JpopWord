package com.dane.jpopword.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class SharedPrefControl {
    public static HashSet<String> loadDataSetByKeyInSharedPref(Context context, String fileName, String key){
        SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        HashSet<String> stringSet = new HashSet<String>(pref.getStringSet(key, new HashSet<String>())); // "favorite"이라는 키로 저장되어있던 스트링셋 불러오고
        Log.d("Load Hearts sharedPref","load hearts: " + stringSet);
        return stringSet;
    }

    public static void saveDataSetByKeyInSharedPref(Context context, String fileName, String key, Set<String> dataToSave){
        SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit(); // 에디터를 열어서
        editor.putStringSet(key, (Set<String>) dataToSave);
        editor.apply(); // 변경 완료. commit()은 동기 방식이라서 apply를 권장.
        Log.d("Load Hearts sharedPref","save hearts: " + dataToSave);
    }
}
