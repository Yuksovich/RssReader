package yuriy.rssreader.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;

import java.io.Closeable;
import java.util.ArrayList;


public final class DBReader implements Closeable {

    private final ArrayList<SingleRSSEntry> listOfEntries;
    private final SQLiteDatabase database;
    private final DatabaseOpenHelper dbOpenHelper;
    private Cursor cursor;

    private static final String[] WITHOUT_ARGUMENTS = null;
    private final static String SELECTION_BY_ITEM_LINK = TableColumns.COLUMN_NAME_ITEM_LINK + " = ?";
    private static final String SELECTION_BY_CHANNEL_TITLE = TableColumns.COLUMN_NAME_CHANNEL_TITLE + " = ?";
    private final static String SORT_ORDER = TableColumns.COLUMN_NAME_ITEM_PUB_DATE + " DESC";
    private final static String[] COLUMNS_ALL = null;
    private final static String SELECTION_ALL = null;
    private final static String[] SELECTION_ARGS_ALL = null;
    private final static String GROUP_BY_ALL = null;
    private final static String HAVING_ALL = null;
    private final static String LIMIT_ALL = null;
    private final static String ALL_CHANNELS = "ALL_CHANNELS";

    public DBReader(@NonNull final Context context) throws SQLException {
        dbOpenHelper = new DatabaseOpenHelper(context);
        listOfEntries = new ArrayList<>();
        database = dbOpenHelper.getReadableDatabase();
    }

    public ArrayList<SingleRSSEntry> read() throws SQLException, DatabaseIsEmptyException {
        return read(ALL_CHANNELS);
    }

    public ArrayList<SingleRSSEntry> read(@Nullable final String channels) throws SQLException, DatabaseIsEmptyException {
        if (ALL_CHANNELS.equals(channels)) {
            cursor = getCursor(WITHOUT_ARGUMENTS);
        } else {
            cursor = getChannelFilteredCursor(channels);
        }

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                final String channelTitle = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_TITLE));
                final String channelImageURL = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_IMAGE_URL));
                final String channelDescription = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_DESCRIPTION));
                final String itemLink = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_LINK));
                final String itemTitle = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_TITLE));
                final String itemDescription = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_DESCRIPTION));
                final String itemPubDate = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_PUB_DATE));
                final String itemBeenViewed = cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_BEEN_VIEWED));

                listOfEntries.add(new SingleRSSEntry.Builder()
                        .channelTitle(channelTitle)
                        .channelImageURL(channelImageURL)
                        .channelDescription(channelDescription)
                        .itemLink(itemLink)
                        .itemTitle(itemTitle)
                        .itemDescription(itemDescription)
                        .itemPubDate(itemPubDate)
                        .itemBeenViewed(itemBeenViewed)
                        .build());
                cursor.moveToNext();
            }
            closeCursor();

            return listOfEntries;

        } else {
            closeCursor();
            throw new DatabaseIsEmptyException();
        }
    }

    public SingleRSSEntry readSingleEntry(@Nullable final String itemLink) throws SQLException, DatabaseIsEmptyException {

        cursor = getCursor(itemLink);
        if (cursor.moveToFirst()) {
            return new SingleRSSEntry.Builder()
                    .channelTitle(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_TITLE)))
                    .channelImageURL(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_IMAGE_URL)))
                    .channelDescription(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_CHANNEL_DESCRIPTION)))
                    .itemLink(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_LINK)))
                    .itemTitle(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_TITLE)))
                    .itemDescription(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_DESCRIPTION)))
                    .itemPubDate(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_ITEM_PUB_DATE)))
                    .itemBeenViewed(cursor.getString(cursor.getColumnIndex(TableColumns.COLUMN_NAME_BEEN_VIEWED)))
                    .build();
        } else {
            closeCursor();
            throw new DatabaseIsEmptyException();
        }


    }

    Cursor getCursor(@Nullable final String... selectionArgs) throws DatabaseIsEmptyException {

        if (selectionArgs == null) {
            final Cursor currentCursor = database.query(TableColumns.TABLE_NAME, COLUMNS_ALL, SELECTION_ALL, SELECTION_ARGS_ALL, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
            if (currentCursor.moveToFirst()) {
                return currentCursor;
            } else {
                throw new DatabaseIsEmptyException();
            }
        } else {
            return database.query(TableColumns.TABLE_NAME, COLUMNS_ALL, SELECTION_BY_ITEM_LINK, selectionArgs, GROUP_BY_ALL, HAVING_ALL, SORT_ORDER, LIMIT_ALL);
        }

    }

    private Cursor getChannelFilteredCursor(@Nullable final String... channels) throws DatabaseIsEmptyException {
        final Cursor currentCursor = database.query(
                TableColumns.TABLE_NAME,
                COLUMNS_ALL,
                SELECTION_BY_CHANNEL_TITLE,
                channels,
                GROUP_BY_ALL,
                HAVING_ALL,
                SORT_ORDER,
                LIMIT_ALL);
        if (currentCursor.moveToFirst()) {
            return currentCursor;
        } else {
            throw new DatabaseIsEmptyException();
        }
    }

    private void closeCursor() {
        if (cursor != null) {
            cursor.close();
        }
    }

    public void close() {
        if (dbOpenHelper != null) {
            dbOpenHelper.close();
        }
    }
}
