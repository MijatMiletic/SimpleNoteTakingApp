package com.example.recordsreboot;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recordsreboot.NoteContract.NoteEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener {

    /**
     * Request codes for startActivityForResult
     */
    private final int ADD_NOTE_REQUEST_CODE = 1;
    private final int EDIT_REQUEST_CODE = 2;

    private Calendar cal;
    private SQLiteDatabase db;

    /**
     * Custom adapter for recycler view
     */
    private NoteAdapter adapter;

    /**
     * Date that is showing at the top of main activity
     */
    private Date showingDate;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateTextView = findViewById(R.id.showingDateTextView);

        cal = Calendar.getInstance();
        changeDateTextView(new Date());

        // Get database from helper
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        // set up the adapter and recycler view
        adapter = new NoteAdapter(this, queryNotes(showingDate));
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /* ========== ON CLICK METHODS ============*/


    /**
     * Listener for startActivityForResult. It handles events when
     * add note activity and edit note activity call finish()
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }

        switch (requestCode){
            case ADD_NOTE_REQUEST_CODE:
                ContentValues values = new ContentValues();
                values.put(NoteEntry.COLUMN_TIME_STAMP, data.getLongExtra(NoteEntry.COLUMN_TIME_STAMP, 0));
                values.put(NoteEntry.COLUMN_CONTENT, data.getStringExtra(NoteEntry.COLUMN_CONTENT));
                values.put(NoteEntry.COLUMN_TITLE, data.getStringExtra(NoteEntry.COLUMN_TITLE));
                db.insert(NoteEntry.TABLE_NAME, null, values);
                showNotes(new Date());
                // scroll to the bottom
                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                break;
            case EDIT_REQUEST_CODE:
                String newContent = data.getStringExtra(NoteEntry.COLUMN_CONTENT);
                String newTitle = data.getStringExtra(NoteEntry.COLUMN_TITLE);
                long id = data.getLongExtra(NoteEntry._ID, 0);
                editNoteDB(id, newTitle, newContent);
                showNotes(new Date());
                break;
        }
    }


    /**
     * Called when user clicks on plus button.
     * @param view
     */
    public void onAddNewNote(View view){
        Intent intent = new Intent(this, AddNoteActivity.class);
        intent.putExtra(NoteContract.NoteEntry.COLUMN_TIME_STAMP, System.currentTimeMillis());
        startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
    }


    /**
     * Called when user clicks on right arrow at the top
     * @param view
     */
    public void onNextDate(View view){
        cal.setTime(showingDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date newDate = new Date(cal.getTimeInMillis());
        // do nothing if the user tries to go into the future
        if(newDate.before(new Date())){
            changeDateTextView(newDate);
            showNotes(newDate);
        }
    }

    /**
     * Called when user clicks on left arrow at the top
     * @param view
     */
    public void onPrevDate(View view){
        cal.setTime(showingDate);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        changeDateTextView(cal.getTime());
        showNotes(cal.getTime());
    }

    /**
     * Called when user clicks on
     * @param view
     */
    public void onShowNote(View view){
        Intent intent = new Intent(this, NoteView.class);
        // add the title to the intent, needed for later when updating database
        long id = (Long) view.getTag();
        TextView titleTextView = view.findViewWithTag(id);
        intent.putExtra(NoteEntry.COLUMN_TITLE, titleTextView.getText().toString());

        // query the db for a note
        Cursor cursor = db.query(NoteEntry.TABLE_NAME,
                new String[] {NoteEntry.COLUMN_TIME_STAMP, NoteEntry.COLUMN_CONTENT},
                NoteEntry._ID + " = " +id,
                null,
                null,
                null,
                null);
        if(cursor.getCount() <= 0){
            return;
        }

        // add time to intent
        cursor.moveToFirst();
        long time = cursor.getLong(cursor.getColumnIndex(NoteEntry.COLUMN_TIME_STAMP));
        intent.putExtra(NoteEntry.COLUMN_TIME_STAMP, time);
        // add content to intent
        String content = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_CONTENT));
        intent.putExtra(NoteEntry.COLUMN_CONTENT, content);
        cursor.close();
        startActivity(intent);
    }

    /**
     * Starts when user clicks on three dots on the far left
     * in the recycler view.
     * @param view
     */
    public void onOptions(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.edit_popup_menu);

        // create an intent that will store the
        // tag of the clicked view so that it
        // knows what note to delete
        Intent intent = new Intent();
        intent.putExtra("tag", (Long) view.getTag());
        popupMenu.getMenu().getItem(0).setIntent(intent);
        popupMenu.getMenu().getItem(1).setIntent(intent);

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    /**
     * Implementation for the popup menu in the recycler view
     * @param menuItem
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        long id = menuItem.getIntent().getLongExtra("tag", 0);

        switch(menuItem.getItemId()){
            case R.id.menu_item_delete:
                deleteNote(id);
                showNotes(new Date());
                return true;
            case R.id.menu_item_edit:
                editNote(id);
                return true;
            default:
                return false;
        }
    }

    /**
     * Called when user clicks on date text view at the top.
     * Shows date picker dialog
     * @param view
     */
    public void onPickDate(View view){
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.show(getSupportFragmentManager(), "Pick a date");
    }

    /**
     * Called when user picks a date that was made in onPickDate.
     * Sets given date in date text view at the top
     * @param datePicker
     * @param y
     * @param m
     * @param d
     */
    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        cal.set(y, m, d);
        showNotes(cal.getTime());
    }

    /* ========== PRIVATE HELPER METHODS ============*/


    /**
     * Called when clicked on edit in a pop up menu.
     * Gets data for a clicked note and starts edit note activity.
     * @param id
     */
    private void editNote(long id){
        Cursor cursor = getNote(id);
        if(cursor.getCount() < 1){
            return;
        }

        cursor.moveToFirst();
        Intent intent = new Intent(this, EditNoteActivity.class);
        // set id
        intent.putExtra(NoteEntry._ID, id);
        // set title
        String title = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TITLE));
        intent.putExtra(NoteEntry.COLUMN_TITLE, title);
        // set content
        String content = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_CONTENT));
        intent.putExtra(NoteEntry.COLUMN_CONTENT, content);

        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    /**
     * Deletes a note from database
     * @param id
     */
    private void deleteNote(long id){
        db.delete(NoteEntry.TABLE_NAME, NoteEntry._ID + " = " + id, null);
    }

    /**
     * Changes the text view for date
     * @param date
     */
    private void changeDateTextView(Date date){
        showingDate = date;
        dateTextView.setText(new SimpleDateFormat("E dd.MM.yyyy").format(showingDate));
    }

    /**
     * Shows notes for a given date.
     * @param date
     */
    private void showNotes(Date date){
        changeDateTextView(date);
        Cursor cursor = queryNotes(date);
        adapter.setCursor(cursor);
    }

    /**
     * Returns a cursor with a single note inside
     * if it exists
     * @param id
     * @return
     */
    private Cursor getNote(long id){
        return db.query(
                NoteEntry.TABLE_NAME,
                null,
                NoteEntry._ID + " = " + id,
                null,
                null,
                null,
                null);
    }


    /**
     * Updates ocntent and title of a note with a given id.
     * @param id
     * @param newTitle
     * @param newContent
     */
    private void editNoteDB(long id, String newTitle, String newContent){
        db.execSQL("UPDATE " + NoteEntry.TABLE_NAME  +
                " SET " + NoteEntry.COLUMN_CONTENT + " = \"" + newContent +
                "\", " + NoteEntry.COLUMN_TITLE + " = \"" + newTitle +
                "\" WHERE " + NoteEntry._ID + " = " + id + ";");
    }

    /**
     * Preforms a query for a given date and returns the result cursor
     * @param date
     * @return
     */
    private Cursor queryNotes(Date date){
        // Get the start of the current day in miliseconds
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year, month, day, 0, 0, 0);
        long startDay = calendar.getTimeInMillis();
        calendar.set(year, month, day+1, 0, 0, 0);
        long endDay = calendar.getTimeInMillis();

        // get data from database
        return db.rawQuery("SELECT * FROM " + NoteEntry.TABLE_NAME +
                " WHERE " + NoteEntry.COLUMN_TIME_STAMP + " BETWEEN " + startDay + " AND " + endDay + ";", null);
    }


}
