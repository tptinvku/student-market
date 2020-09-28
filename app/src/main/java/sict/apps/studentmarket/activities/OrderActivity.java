package sict.apps.studentmarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.ui.order.OrderEmptyUI;
import sict.apps.studentmarket.ui.order.OrderUI;
import sict.apps.studentmarket.ultil.CheckConnection;
import sict.apps.studentmarket.ultil.MySqlite;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = OrderActivity.class.getSimpleName();
    private Toolbar toolbar;
    private TextView mTitle;
    private MySqlite database;
    private Cursor cs;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        database = new MySqlite(this);
        if(CheckConnection.haveNetworkConnection(this)){
            mapping();
            initToolbar();
            loadSqlite();
            if(checkStatusOrders()){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_orders_container, OrderUI.newInstance())
                        .commit();
            }else{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_orders_container, OrderEmptyUI.newInstance())
                        .commit();
            }
        }else {
            CheckConnection.showToastShort(this, "Bạn hãy kiểm tra lại kết nối");
            finish();
        }
    }
    private void loadSqlite(){
        MySqlite mySqlite = new MySqlite(this);
        Cursor cs = mySqlite.get("SELECT * FROM authorization");
        if(cs.moveToNext()){
            String tk = cs.getString(0);
            String _id = cs.getString(1);
            String name = cs.getString(2);
            int phone = cs.getInt(3);
            boolean gender = cs.getInt(4) > 0 ? true: false;
            String email = cs.getString(5);
            user = new User(tk, _id, name, phone, gender, email);
        }
        cs.close();
        mySqlite.close();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.cart_toolbar);
        mTitle = (TextView) findViewById(R.id.toolbar_title);
    }
    private void initToolbar(){
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        mTitle.setText(R.string.lblCart);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean checkStatusOrders(){
        if(user!=null){
            cs = database.get(String.format("SELECT * FROM cart WHERE  userId = '%s'",user.get_id()));
            if (cs.moveToNext()){
                return true;
            }
            return false;
        }else {
            cs = database.get("SELECT * FROM cart WHERE  userId = 'GUEST'");
            if (cs.moveToNext()){
                return true;
            }
            return false;
        }
    }
}
