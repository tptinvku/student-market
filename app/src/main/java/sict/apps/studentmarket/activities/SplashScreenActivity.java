package sict.apps.studentmarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Category;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.ultil.PreLoadApi;
import sict.apps.studentmarket.ultil.CheckConnection;
import sict.apps.studentmarket.ultil.Server;

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = SplashScreenActivity.class.getSimpleName();
    private List<Category> lsCategories = new ArrayList<Category>();
    private final int TIME_OUT = 101;
    private TextView textView;
    private ProgressBar progressBar;
    private PreLoadApi apiListener;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(CheckConnection.haveNetworkConnection(this)) {
            mapping();
            connectApi();
            startHeavyProcessing();
        }else {
            CheckConnection.showToastShort(this, "Bạn hãy kiểm tra lại kết nối");
            finish();
        }
    }
    private void mapping(){
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }
    private void connectApi(){
        apiListener = new PreLoadApi(this);
        apiListener.getListCategories(lsCategories);
        user = apiListener.getUserProfile();
    }
    private void startHeavyProcessing(){
        new LongOperation().execute();
    }

    class LongOperation extends AsyncTask<Void, Integer, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=0; i<TIME_OUT; i+=1){
                try {
                    publishProgress(i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            Gson gson = new Gson();
            String jsonCategories = gson.toJson(lsCategories);
            Log.d(TAG, "list categories: " + jsonCategories);
            i.putExtra("lsCategories", jsonCategories);
            String jsonUser = gson.toJson(user);
            i.putExtra("objUser", jsonUser);
            startActivity(i);
            finish();

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int giatri=values[0];
            textView.setText(giatri+"%");
            progressBar.setProgress(giatri);
        }
    }
}
