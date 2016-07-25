package kr.kodev.healthbyme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.LocaleDisplayNames;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView tv_profile_name;
    TextView tv_profile_kcal;
    String str = "";
    SharedPreferences pref;
    ListView listView;
    ListViewAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.nowlayout_main);
        listView = (ListView) findViewById(R.id.listview);

        circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        tv_profile_name = (TextView) findViewById(R.id.tv_profile_name);
        tv_profile_kcal = (TextView) findViewById(R.id.tv_profile_kcal);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        get_user(pref.getString("user_id", ""));
        get_food(pref.getString("user_id", ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_write:
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);

                AlertDialog.Builder buider = new AlertDialog.Builder(this);
                buider.setTitle("Member Information");
                buider.setIcon(android.R.drawable.ic_menu_add);
                buider.setView(dialogView);
                buider.setPositiveButton("Complite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText edit_food = (EditText) dialogView.findViewById(R.id.dialog_food);
                        EditText edit_kcal = (EditText) dialogView.findViewById(R.id.dialog_kcal);
                        EditText edit_description = (EditText) dialogView.findViewById(R.id.dialog_description);
                        String user_id = pref.getString("user_id", "");
                        String food = edit_food.getText().toString();
                        int kcal = Integer.parseInt(edit_kcal.getText().toString());
                        String description = edit_description.getText().toString();
                        write_food(user_id, food, kcal, description);
                        Toast.makeText(MainActivity.this, "새로운 멤버를 추가했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                buider.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "멤버 추가를 취소합니다", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = buider.create();

                dialog.setCanceledOnTouchOutside(false);

                dialog.show();
                break;
            case R.id.action_refresh:
                get_food(pref.getString("user_id", ""));

                break;
        }

        return super.onOptionsItemSelected(item);
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

    public void get_food(String user_id){
        ListService.api().foods_get(user_id).enqueue(new Callback<List<FoodItem>>() {
            @Override
            public void onResponse(Call<List<FoodItem>> call, Response<List<FoodItem>> response) {
                if (response != null && response.isSuccess() && response.body() != null) {
                    List<FoodItem> foodItemList = response.body();
                    ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String, String>>();
                    int all_kcal = 0;
                    for(FoodItem foodItem : foodItemList){
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("food_id",foodItem.food);
                        hashMap.put("food",foodItem.food);
                        hashMap.put("kcal",""+foodItem.kcal);
                        hashMap.put("description",foodItem.description);
                        hashMap.put("date",foodItem.eat_time);
                        arrayList.add(hashMap);
                        all_kcal+=foodItem.kcal;
                    }
                    mAdapter = new ListViewAdapter(MainActivity.this,arrayList);
                    listView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    tv_profile_kcal.setText("오늘 먹은 칼로리 : "+all_kcal+"Kcal");

                }
            }

            @Override
            public void onFailure(Call<List<FoodItem>> call, Throwable t) {

            }
        });
    }

    public void write_food(String user_id, String food, int kcal, String descripton) {
        Log.d("LOG", "user+id" + user_id);
        ListService.api().foods_post(user_id, food, "" + kcal, descripton).enqueue(new Callback<FoodItem>() {
            @Override
            public void onResponse(Call<FoodItem> call, Response<FoodItem> response) {
                if (response != null && response.isSuccess() && response.body() != null) {
                    FoodItem foodItem = response.body();
                    Log.d("LOG", "result: " + foodItem.result);
                }
            }

            @Override
            public void onFailure(Call<FoodItem> call, Throwable t) {

            }
        });
    }

    private class ViewHolder {
        TextView mFood;
        TextView mDate;
        TextView mDescroption;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<HashMap<String, String>> mListData = new ArrayList<HashMap<String, String>>();

        public ListViewAdapter(Context mContext, ArrayList<HashMap<String,String>> arrayList) {
            super();
            this.mContext = mContext;
            this.mListData = arrayList;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_food_list, null);

                holder.mFood = (TextView) convertView.findViewById(R.id.row_food);
                holder.mDate = (TextView) convertView.findViewById(R.id.row_date);
                holder.mDescroption = (TextView) convertView.findViewById(R.id.row_description);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            HashMap<String,String> hashMap = mListData.get(position);



            holder.mFood.setText(hashMap.get("food")+" ("+hashMap.get("kcal")+"Kcal)");
            holder.mDate.setText(hashMap.get("date"));
            holder.mDescroption.setText(hashMap.get("description"));

            return convertView;
        }
        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }
    }
}
