package sict.apps.studentmarket.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Message;
import sict.apps.studentmarket.models.RoomChat;
import sict.apps.studentmarket.ultil.MySocket;

public class MessengerActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private EditText mInputMessage;
    private ImageView btn_send;
    private String sellerName;
    private Socket mSocket;
    private RoomChat room;
    private String cvstId;
    private String sl_id;
    private String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getSocket();
        mSocket.connect();
        mSocket.on("server-send-cvstId", onConversation);
        mSocket.on("server-send-message", onNewMessage);
        receiveData();
        mapping();
        initToolbar();
        catchEvent();
    }
    private void mapping() {
        toolbar = (Toolbar) findViewById(R.id.messenger_toolbar);
        title = (TextView) findViewById(R.id.toolbar_title);
        mInputMessage = (EditText) findViewById(R.id.input_message);
        btn_send = (ImageView) findViewById(R.id.btn_send_mess);
    }
    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        title.setText(sellerName);
    }
    private void receiveData(){
        sellerName = getIntent().getStringExtra("sellerName");
        sl_id = getIntent().getStringExtra("sl_id");
        u_id = getIntent().getStringExtra("userId");
        room = new RoomChat(sl_id, u_id);
        Gson gson = new Gson();
        String roomInfo = gson.toJson(room);
        mSocket.emit("client-create-room", roomInfo);
    }
    private void catchEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }
    private void attemptSend() {
        String message = mInputMessage.getText().toString().trim();
        Message ms = new Message(cvstId, u_id, message);
        Gson gson = new Gson();
        String objContent = gson.toJson(ms);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mInputMessage.setText("");
        mSocket.emit("client-send-message", objContent);
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String content = data.getString("message");
                        Toast.makeText(MessengerActivity.this, content, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener onConversation  = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        cvstId = data.getString("_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("client-send-message", onNewMessage);
    }
}
