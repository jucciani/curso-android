package com.ar.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.ar.dto.Item;

/**
 * Created by jucciani on 04/05/14.
 */
public class ItemDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Item.db";

    public ItemDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemContract.ItemEntry.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL(ItemContract.ItemEntry.SQL_CREATE_ENTRIES);
        onCreate(db);
    }

    public void saveItem(Item item){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME_ITEM_ID, item.getId());
        values.put(ItemContract.ItemEntry.COLUMN_NAME_PRICE, item.getPrice());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                ItemContract.ItemEntry.TABLE_NAME,
                null,
                values);
    }

    public Item readItem(String itemId){
        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ItemContract.ItemEntry.COLUMN_NAME_ITEM_ID,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE
        };
        // Define 'where' part of query.
        String selection = ItemContract.ItemEntry.COLUMN_NAME_ITEM_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(itemId) };

        Cursor c = db.query(
                ItemContract.ItemEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        Item item = null;
        if(c.getCount() >0){
            item = new Item(itemId);
            c.moveToFirst();
            item.setPrice(c.getDouble(
                    c.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_PRICE)
            ));
        }
        return item;
    }

    public void deleteItem(String itemId){
        SQLiteDatabase db = getReadableDatabase();

        // Define 'where' part of query.
        String selection = ItemContract.ItemEntry.COLUMN_NAME_ITEM_ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(itemId) };
        // Issue SQL statement.
        db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
    }

    public static final class ItemContract {
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public ItemContract() {}

        /* Inner class that defines the table contents */
        public static abstract class ItemEntry implements BaseColumns {
            public static final String TABLE_NAME = "item";
            public static final String COLUMN_NAME_ITEM_ID = "itemid";
            public static final String COLUMN_NAME_PRICE = "price";
            private static final String TEXT_TYPE = " TEXT";
            private static final String COMMA_SEP = ",";
            private static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                            ItemEntry._ID + " INTEGER PRIMARY KEY," +
                            ItemEntry.COLUMN_NAME_ITEM_ID + TEXT_TYPE + COMMA_SEP +
                            ItemEntry.COLUMN_NAME_PRICE + TEXT_TYPE + " )";

            private static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;
        }
    }
}
