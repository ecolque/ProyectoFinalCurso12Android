package com.nextu.proyectofinal;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    //atributos para facebook
    private CallbackManager callbackManager;
    private LoginButton loginButton;

    //ad view para banner
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("SKD Facebook");
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        //codigo has1 de la app
        getFbKeyHas("88:C3:3F:05:49:4B:36:23:A6:D7:28:20:7B:3C:6C:73:29:8C:A8:87");
        setContentView(R.layout.activity_main);

        initFacebook();
        initBanner();

    }

    private void initFacebook(){
        //conexion con activity_main
        loginButton = (LoginButton) findViewById(R.id.login);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {//llamada de callback a facebook
            @Override
            public void onSuccess(LoginResult loginResult) {

                final AccessToken accessToken = loginResult.getAccessToken();
                GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        Toast.makeText(MainActivity.this, "Iniciado por :" + user.optString("name"), Toast.LENGTH_SHORT).show();
                    }
                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Inicio de sesión Cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Inicio de session No Exitoso", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void initBanner(){
        //conexion del adView para ajuntar el banner
        adView = (AdView) findViewById(R.id.banner_ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }

    //metodo de verificacion de las llaves entre la app
    private void getFbKeyHas(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES
            );
            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHas :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("KeyHas: " +Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){

        }catch (NoSuchAlgorithmException e){

        }
    }

    //resultado de la actividad de facebook
    protected void onActivityResult(int reqCode, int resCode, Intent i){
        callbackManager.onActivityResult(reqCode, resCode, i);
    }

    //destruye el ad view adview de banner
    @Override
    protected void onDestroy() {
        if(adView != null){
            adView.destroy();
        }
        super.onDestroy();
    }

    //pone en pausa el adview de banner
    @Override
    protected void onPause() {
        if(adView != null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(adView != null){
            adView.resume();
        }
        super.onResume();
    }
}
