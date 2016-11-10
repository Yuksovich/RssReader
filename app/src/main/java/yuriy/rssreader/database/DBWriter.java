package yuriy.rssreader.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;
import java.util.ArrayList;


public final class DBWriter implements Closeable {

       private final static String FALSE_FLAG = "false";
    private static final String TRUE_FLAG = "true";
    private static final String WHERE_CLAUSE = RSSEntryColumns.COLUMN_NAME_ITEM_LINK + " = ?";
    private final RssDBOpenHelper dbOpenHelper;
    private final Context context;
    private int newEntriesCount = 0;
    private SQLiteDatabase database;

    public DBWriter(final Context context) {
        this.context = context;
        dbOpenHelper = new RssDBOpenHelper(context);

    }

    public void populate(final ArrayList<SingleRSSEntry> dataArrayList) throws SQLException {
        final DuplicateChecker duplicateChecker = new DuplicateChecker(context);

        final ArrayList<SingleRSSEntry> croppedDataArrayList = duplicateChecker.cropDuplicateEntries(dataArrayList);
        final ContentValues values = new ContentValues();
        database = dbOpenHelper.getWritableDatabase();

        for (SingleRSSEntry entry : croppedDataArrayList) {
            values.put(RSSEntryColumns.COLUMN_NAME_CHANNEL_TITLE, entry.getChannelTitle());
            values.put(RSSEntryColumns.COLUMN_NAME_CHANNEL_DESCRIPTION, entry.getChannelDescription());
            values.put(RSSEntryColumns.COLUMN_NAME_CHANNEL_IMAGE_URL, entry.getChannelImageURL());
            values.put(RSSEntryColumns.COLUMN_NAME_ITEM_LINK, entry.getItemLink());
            values.put(RSSEntryColumns.COLUMN_NAME_ITEM_TITLE, entry.getItemTitle());
            values.put(RSSEntryColumns.COLUMN_NAME_ITEM_DESCRIPTION, entry.getItemDescription());
            values.put(RSSEntryColumns.COLUMN_NAME_ITEM_PUB_DATE, entry.getItemPubDate());
            values.put(RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED, FALSE_FLAG);
            try {
                database.beginTransaction();
                database.insert(RSSEntryColumns.TABLE_NAME, RSSEntryColumns.COLUMN_NAME_NULLABLE, values);
                newEntriesCount++;
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
        database.close();

    }

    public void setEntryBeenViewed(String itemLink) throws SQLException {
        ContentValues value = new ContentValues();
        value.put(RSSEntryColumns.COLUMN_NAME_BEEN_VIEWED, TRUE_FLAG);
        database = dbOpenHelper.getWritableDatabase();
        database.update(RSSEntryColumns.TABLE_NAME, value, WHERE_CLAUSE, new String[]{itemLink});
    }

    public void close() {
        dbOpenHelper.close();
    }

    public int getNewEntriesCount() {
        return newEntriesCount;
    }

    public void delete(final String itemLink) throws SQLException{
        try {
            String[] whereArgs = {itemLink};
            database = dbOpenHelper.getWritableDatabase();
            database.delete(RSSEntryColumns.TABLE_NAME, WHERE_CLAUSE, whereArgs);
        }finally {
            database.close();
        }

    }

}

