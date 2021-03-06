package yuriy.rssreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;


final class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String CREATE_TABLE = "CREATE TABLE " +
            TableColumns.TABLE_NAME + " (" + TableColumns._ID + " integer primary key autoincrement, " +
            TableColumns.COLUMN_NAME_CHANNEL_URL + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_CHANNEL_TITLE + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_CHANNEL_IMAGE_URL + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_CHANNEL_DESCRIPTION + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_ITEM_TITLE + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_ITEM_LINK + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_ITEM_DESCRIPTION + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_ITEM_PUB_DATE + TEXT_TYPE + " ," +
            TableColumns.COLUMN_NAME_BEEN_VIEWED + TEXT_TYPE + ")";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "RssReader.db";
    private static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TableColumns.TABLE_NAME;

    DatabaseOpenHelper(final @NonNull Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (db == null) {
            return;
        }
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }

}
