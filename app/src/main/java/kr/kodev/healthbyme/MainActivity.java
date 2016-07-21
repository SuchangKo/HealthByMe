package kr.kodev.healthbyme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    CircleImageView circleImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.nowlayout_main);
        LoginButton loginButton;
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        // If using in a fragment
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);

        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    final JSONObject object,
                                    GraphResponse response) {
                                try{

                                    Log.d("FBLog",object.toString());
                                    Log.d("FBLog",object.getString("id"));
                                    Log.d("FBLog",object.getString("name"));
                                    Log.d("FBLog",object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    new AsyncTask<Void,Void,Void>(){
                                        Bitmap tmp_Profilebmp;
                                        @Override
                                        protected Void doInBackground(Void... voids) {
                                            try {
                                                tmp_Profilebmp = getFacebookProfilePicture(object.getString("id"));
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            circleImageView.setImageBitmap(tmp_Profilebmp);
                                        }
                                    }.execute();


                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        //startActivity(new Intent(getApplicationContext(),ToDoActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap getFacebookProfilePicture(String userID) {
        try {
            Log.d("FBLog","https://graph.facebook.com/" + userID + "/picture?type=large");
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            return bitmap;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
