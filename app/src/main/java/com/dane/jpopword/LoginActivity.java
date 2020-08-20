package com.dane.jpopword;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    // 파이어베이스 로그인 관련
    int RC_SIGN_IN = 9000;
    FirebaseAuth mAuth;


    CallbackManager mCallbackManager; // 페이스북 추가 중
    LoginButton btnLoginFacebook;

    Button btnFakeFacebook;

    void LogFirebaseAnalytics(String ITEM_ID, String ITEM_NAME, String CONTENT_TYPE){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, ITEM_ID);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ITEM_NAME);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, CONTENT_TYPE);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this); // Firebase Analytics 추가 중

        Button btnLoginGoogle = (Button)findViewById(R.id.login_btn_google);
        btnLoginGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                LogFirebaseAnalytics("LOGIN_ACT_GOOGLE","LOGIN_ACT_GOOGLE","Click");
                // login...
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.AppThemeFirebaseAuth)
                                .setLogo(R.drawable.ch_dalmabal)
                                .build(),
                        RC_SIGN_IN);
            }
        });

        // 페이스북 추가 중
        mCallbackManager = CallbackManager.Factory.create();
        btnLoginFacebook = findViewById(R.id.login_btn_facebook);
        btnLoginFacebook.setPermissions("email");
        btnLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "facebook:onSuccess:" + loginResult);
                facebookToken = loginResult.getAccessToken();
                handleFacebookAccessToken(facebookToken);

                //
                Log.d("Facebook Login", ""+ AccessToken.getCurrentAccessToken().getUserId());
                //Log.d("Facebook Login", ""+Profile.getCurrentProfile().getName());
                finish();
            }
            @Override
            public void onCancel() {
                Log.d("Facebook Login", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Login", "facebook:onError", error);
            }
        });
        //Our custom Facebook button
        btnFakeFacebook = (Button) findViewById(R.id.login_btn_fake_facebook);

        TextView textViewCancel = (TextView)findViewById(R.id.login_textView_cancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogFirebaseAnalytics("LOGIN_ACT_CANCEL","LOGIN_ACT_CANCEL","Click");
                finish();
            }
        });
    }

    public void onClickFacebookButton(View view) {
        if (view == btnFakeFacebook) {
            btnLoginFacebook.performClick();
            LogFirebaseAnalytics("LOGIN_ACT_FACEBOOK","LOGIN_ACT_FACEBOOK","Click");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this,"LOGIN : "+user.getEmail(),Toast.LENGTH_SHORT).show();

                finish();
                // Youtube->Quiz로 가는 코드블럭
                /*Intent intent = new Intent(LoginActivity.this, QuizActivity.class);
                intent.putExtra("selectedSong", songFromSongsAct);
                intent.putParcelableArrayListExtra("words", wordsFromSongAct);
                Log.d("Youtube Act.", "Youtube->Quiz로 보내는 단어 샘플: "+wordsFromSongAct.get(0).wordJap+", "+wordsFromSongAct.get(1).wordJap);
                startActivity(intent);*/
                // ...
            } else {
                // Sign in failed.
                Toast.makeText(this,"LOGIN ERROR",Toast.LENGTH_SHORT).show();
            }
        }

        // 페이스북 추가 중
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // 페이스북 추가 중
    AccessToken facebookToken;
    private void handleFacebookAccessToken(AccessToken token) {

        mAuth = FirebaseAuth.getInstance();

        Log.d("FACEBOOK LOGIN", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FACEBOOK LOGIN", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            //
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FACEBOOK LOGIN", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
