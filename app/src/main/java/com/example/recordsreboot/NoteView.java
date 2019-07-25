package com.example.recordsreboot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        Intent intent = getIntent();
        //set title
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(intent.getStringExtra(NoteContract.NoteEntry.COLUMN_TITLE));
        // set content
        TextView noteTextView = findViewById(R.id.contentTextView);
        noteTextView.setText(intent.getStringExtra(NoteContract.NoteEntry.COLUMN_CONTENT));
        // set time
        TextView timeTextView = findViewById(R.id.timeTextView);
        long time = intent.getLongExtra(NoteContract.NoteEntry.COLUMN_TIME_STAMP,0);
        String strTime = new SimpleDateFormat("E dd.MM.yyyy hh:mm").format(new Date(time));
        timeTextView.setText(strTime);
    }
}
