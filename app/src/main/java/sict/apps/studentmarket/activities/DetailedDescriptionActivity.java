package sict.apps.studentmarket.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sict.apps.studentmarket.R;

public class DetailedDescriptionActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView textView;
    private FloatingActionButton FAB_done;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_description);
        mapping();
        initToolbar();
        catchEvent();
    }
    private void mapping(){
        toolbar = (Toolbar) findViewById(R.id.detail_description_toolbar);
        textView = (TextView) findViewById(R.id.toolbar_title);
        FAB_done = (FloatingActionButton) findViewById(R.id.fab_done);
        editText = (EditText) findViewById(R.id.description);
    }
    private void initToolbar(){
        textView.setText(R.string.detail_description);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
    }
    private void catchEvent(){
        FAB_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("description", editText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
