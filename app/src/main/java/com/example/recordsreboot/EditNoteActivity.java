package com.example.recordsreboot;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Intent intent = getIntent();
        String content = intent.getStringExtra(NoteContract.NoteEntry.COLUMN_CONTENT);
        String title = intent.getStringExtra(NoteContract.NoteEntry.COLUMN_TITLE);
        id = intent.getLongExtra(NoteContract.NoteEntry._ID, 0);

        contentEditText = findViewById(R.id.editNoteEditText);
        contentEditText.setText(content);

        titleEditText = findViewById(R.id.editTitleEditText);
        titleEditText.setText(title);
    }

    public void onSave(View view){
        String newContent = contentEditText.getText().toString();
        String newTitle = titleEditText.getText().toString();

        if(newTitle.trim().isEmpty() || newContent.trim().isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NO NO");
            builder.setMessage("You can't have an empty title or an empty note");
            builder.create().show();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_TITLE, newTitle);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_CONTENT, newContent);
        intent.putExtra(NoteContract.NoteEntry._ID, id);
        setResult(RESULT_OK, intent);
        finish();
    }
}
