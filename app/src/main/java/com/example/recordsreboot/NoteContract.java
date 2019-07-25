package com.example.recordsreboot;

import android.provider.BaseColumns;

public class NoteContract {

    private NoteContract(){}

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_TIME_STAMP = "time_stamp";
        public static final String COLUMN_TITLE = "title";
    }
}
