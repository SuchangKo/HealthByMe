package kr.kodev.healthbyme;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by SuchangKo on 2016. 7. 24..
 */
public final class ListService extends BaseService {
    public static ListAPI api() {
        return (ListAPI) retrofit(ListAPI.class);
    }

    public interface ListAPI {
        @GET("/users")
        Call<UserItem> users_get(@Query("user_id") String user_id);

        @FormUrlEncoded
        @POST("/users")
        Call<UserItem> users_post(@Field("fb_id") String fb_id, @Field("name") String name, @Field("nickname") String nickname, @Field("gcm") String gcm);

        @FormUrlEncoded
        @PUT("/users")
        Call<UserItem> users_put(@Field("user_id") String user_id, @Field("nickname") String nickname);

        @FormUrlEncoded
        @DELETE("/users")
        Call<UserItem> users_delete(@Field("user_id") String user_id);

        @GET("/foods")
        Call<FoodItem> foods_get(@Query("user_id") String user_id);

        @FormUrlEncoded
        @POST("/foods")
        Call<FoodItem> foods_post(@Field("user_id") String user_id, @Field("food") String food, @Field("kcal") String kcal, @Field("description") String description);

        @FormUrlEncoded
        @PUT("/foods")
        Call<FoodItem> foods_put(@Field("food_id") String food_id, @Field("food") String food, @Field("kcal") String kcal, @Field("description") String description);

        @FormUrlEncoded
        @DELETE("/foods")
        Call<FoodItem> foods_delete(@Field("food_id") String food_id);

    }
}