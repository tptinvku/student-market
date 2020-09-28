package sict.apps.studentmarket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.database.AbstractCursor;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.Result;
import sict.apps.studentmarket.models.Search;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.ui.search.OriginSearchUI;
import sict.apps.studentmarket.ui.search.SearchUI;
import sict.apps.studentmarket.ultil.MySocket;
import sict.apps.studentmarket.ultil.MySqlite;
import sict.apps.studentmarket.vm.SearchShareViewModel;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private Toolbar toolbar;
    private EditText txtSearch;
    private Socket mSocket;
    private SearchShareViewModel viewModel;
    private List<Result> rs = new ArrayList<>();
    private Cursor cs;
    private MySqlite database;
    private String token;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        viewModel = new ViewModelProvider(this).get(SearchShareViewModel.class);
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getSocket();
        loadData();
        mapping();
        initToolbar();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_search_container, OriginSearchUI.newInstance())
                .commit();
    }
    private void loadData(){
        database = new MySqlite(this);
        cs = database.get("SELECT * FROM authorization");
        Gson gson = new Gson();
        if (cs.moveToNext()) {
            token = cs.getString(0);
            String _id = cs.getString(1);
            String name = cs.getString(2);
            int phone = cs.getInt(3);
            boolean gender = cs.getInt(4) > 0 ? true: false;
            String email = cs.getString(5);
            user = new User(token, _id, name, phone, gender, email);
        }
        if(user==null){
            user = new User("", "GUEST", "", 0, true, "");
        }
        cs.close();
        database.close();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mSocket.connect();
        mSocket.on("emit-search", onResultFound);
        Log.d(TAG, "onStart: "+"START");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: "+"PAUSE");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSocket.connect();
        mSocket.on("emit-search", onResultFound);
        Log.d(TAG, "onResume: "+"RESUME");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("search", onResultFound);
        Log.d(TAG, "onDestroy: "+"DESTROY");
    }
    private void mapping() {
        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        txtSearch = (EditText) findViewById(R.id.txt_search);
    }
    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String kw = s.toString().trim();
                String userId = user.get_id();
                Search search = new Search(userId, kw);
                Gson gson = new Gson();
                String objSearch =  gson.toJson(search);
                if(!kw.isEmpty()) {
                    mSocket.emit("search", objSearch);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_search_container, SearchUI.newInstance())
                            .commit();
                }else{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_search_container, OriginSearchUI.newInstance())
                            .commit();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                txtSearch.setText("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private Emitter.Listener onResultFound = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        rs.clear();
                        JSONObject test = (JSONObject) args[0];
                        String userId = test.getString("userId");
                        JSONArray jsonArray = test.getJSONArray("result");
                        Log.d(TAG, "run: "+jsonArray.length());
                        if(user.get_id().equals(userId)) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("_id");
                                String categoryId = jsonObject.getString("categoryId");
                                String image = jsonObject.getString("product_imagePath");
                                String postName = jsonObject.getString("product_name");
                                double price = Double.parseDouble(jsonObject.getString("product_price"));
                                Result result = new Result(id, categoryId, image, postName, price);
                                rs.add(result);
                                Log.d(TAG, "run: " + id);
                            }
                            viewModel.setResults(rs);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
}
