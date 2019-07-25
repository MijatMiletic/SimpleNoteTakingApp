package com.example.recordsreboot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {

    private long date;
    private EditText noteEditText;
    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteEditText = findViewById(R.id.newNoteEditText);
        titleEditText = findViewById(R.id.titleEditText);
        if(titleEditText.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        Intent intent = getIntent();
        date = intent.getLongExtra(NoteContract.NoteEntry.COLUMN_TIME_STAMP, 0);

    }

    public void onAdd(View view){
        String title = titleEditText.getText().toString();
        String content = noteEditText.getText().toString();

        if(content.trim().isEmpty() || title.trim().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NO NO");
            builder.setMessage("You can't make an empty note");
            builder.create().show();
        }else{
            Intent intent = new Intent();
            intent.putExtra(NoteContract.NoteEntry.COLUMN_TIME_STAMP, date);
            intent.putExtra(NoteContract.NoteEntry.COLUMN_CONTENT, content);
            intent.putExtra(NoteContract.NoteEntry.COLUMN_TITLE, title);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}
