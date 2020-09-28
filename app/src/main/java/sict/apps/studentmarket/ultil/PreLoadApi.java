package sict.apps.studentmarket.ultil;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;

public class PreLoadApi extends ApiConnect {
    private final String TAG = PreLoadApi.class.getSimpleName();
    private Retrofit retrofit = getRetrofit();
    private APIService apiService = retrofit.create(APIService.class);
    private Context context;
    private User user;
    public PreLoadApi(Context context) {
        this.context = context;
        setUserProfile(this.context);
    }

    private void setUserProfile(Context context){
        MySqlite mySqlite = new MySqlite(context);
        Cursor cs = mySqlite.get("SELECT * FROM authorization");
        if(cs.moveToNext()){
            String token = cs.getString(0);
            String _id = cs.getString(1);
            String name = cs.getString(2);
            int phone = cs.getInt(3);
            boolean gender = cs.getInt(4) > 0 ? true: false;
            String email = cs.getString(5);
            user = new User(token, _id, name, phone, gender, email);
        }
        cs.close();
        mySqlite.close();
    }

    public User getUserProfile() {
        return user;
    }

    public void getListCategories(List<Category> lsCategories){
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.isSuccessful()){
                    for (int i = 0; i < response.body().size() ; i++){
                        String id = response.body().get(i).get_id();
                        String pathName = response.body().get(i).getCategory_imagePath();
                        String name = response.body().get(i).getCategory_name();
                        lsCategories.add(new Category(id, pathName, name));
                    }
                }
                call.cancel();
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        });
    }
}
