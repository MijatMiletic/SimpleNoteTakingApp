package com.example.recordsreboot;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private Cursor cursor;

    public NoteAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.note_element, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String title = cursor.getString(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TITLE));
        holder.noteTextView.setText(title);

        long timeStamp = cursor.getLong(cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_TIME_STAMP));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        holder.timeTextView.setText(String.format("%d:%d", cal.get(cal.HOUR_OF_DAY), cal.get(cal.MINUTE)));

        // set the id's for deleting and showing notes
        long id = cursor.getLong(cursor.getColumnIndex(NoteContract.NoteEntry._ID));
        holder.imageView.setTag(id);
        holder.noteTextView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        public TextView noteTextView;
        public TextView timeTextView;
        public ImageView imageView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTextView = itemView.findViewById(R.id.timeTextView);
            timeTextView = itemView.findViewById(R.id.titleTextView);
            imageView = itemView.findViewById(R.id.deleteBtn);
        }
    }

    public void setCursor(Cursor cursor){
        if(cursor != null && !cursor.isClosed()){
            this.cursor.close();
        }

        this.cursor = cursor;

        if(cursor != null){
            notifyDataSetChanged();
        }
    }

}
