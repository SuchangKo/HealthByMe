package kr.kodev.healthbyme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

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
import org.w3c.dom.Text;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView tv_profile_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.nowlayout_main);
        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        tv_profile_name = (TextView)findViewById(R.id.tv_profile_name);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        get_user(pref.getString("user_id", ""));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public static Bitmap getFacebookProfilePicture(String userID) {
        try {
            Log.d("FBLog", "https://graph.facebook.com/" + userID + "/picture?type=large");
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void get_user(String user_id) {
        Log.d("LOG", "user+id" + user_id);
        ListService.api().users_get(user_id).enqueue(new Callback<UserItem>() {
            @Override
            public void onResponse(Call<UserItem> call, Response<UserItem> response) {
                if (response != null && response.isSuccess() && response.body() != null) {
                    final UserItem item = response.body();
                    Log.d("LOG", response.toString());
                    Log.d("LOG", "" + item.user_id);

                    new AsyncTask<Void, Void, Void>() {
                        Bitmap tmp_bitmap;

                        @Override
                        protected Void doInBackground(Void... voids) {
                            tmp_bitmap = getFacebookProfilePicture("" + item.fb_id);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            circleImageView.setImageBitmap(tmp_bitmap);
                            tv_profile_name.setText(item.name);
                        }
                    }.execute();
                }
            }

            @Override
            public void onFailure(Call<UserItem> call, Throwable t) {

            }
        });
    }
}
