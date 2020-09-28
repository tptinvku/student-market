package sict.apps.studentmarket.ultil;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySqlite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping-cart";
    private static final int DATABASE_VERSION = 1;
    private static final String[] TABLE_NAME = {"cart", "authorization"};

    // columns name
    private static final String KEY_USR_ID = "userId";
    private static final String KEY_PRODUCT_ID = "productId";
    private static final String KEY_CATEGORY_ID = "categoryId";
    private static final String KEY_PRODUCT_NAME = "productName";
    private static final String KEY_PRODUCT_IMG = "productImage";
    private static final String KEY_PRODUCT_QTY = "productQuantity";
    private static final String KEY_PRODUCT_PRICE = "productPrice";
    private static final String KEY_TOTAL_PRICE = "totalPrice";

    private static final String KEY_TOKEN_AUTH = "tokenAuth";
    private static final String KEY_USER_ID = "_id";
    private static final String KEY_USER_NAME = "username";
    private static final String KEY_USER_PHONE = "phone";
    private static final String KEY_USER_GENDER = "gender";
    private static final String KEY_USER_EMAIL = "email";

    public MySqlite(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
        onCreate(db);
    }

    public void post(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public Cursor get(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCartTable = String.format(
                "CREATE TABLE IF NOT EXISTS %s(" +
                        " cartId INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        " %s VARCHAR(100)," +
                        " %s VARCHAR(100)," +
                        " %s VARCHAR(100)," +
                        " %s VARCHAR(150)," +
                        " %s VARCHAR(300)," +
                        " %s INTEGER," +
                        " %s DOUBLE," +
                        " %s DOUBLE)",
                TABLE_NAME[0],
                KEY_USR_ID,
                KEY_PRODUCT_ID,
                KEY_CATEGORY_ID,
                KEY_PRODUCT_NAME,
                KEY_PRODUCT_IMG,
                KEY_PRODUCT_QTY,
                KEY_PRODUCT_PRICE,
                KEY_TOTAL_PRICE);
        String createTokenTable = String.format(
                "CREATE TABLE IF NOT EXISTS %s(" +
                " %s VARCHAR(100) PRIMARY KEY," +
                " %s VARCHAR(100)," +
                " %s VARCHAR(100)," +
                " %s INTEGER," +
                " %s INTEGER," +
                " %s VARCHAR(100))",
                TABLE_NAME[1],
                KEY_TOKEN_AUTH, //0
                KEY_USER_ID,    //1
                KEY_USER_NAME,  //2
                KEY_USER_PHONE, //3
                KEY_USER_GENDER,//4
                KEY_USER_EMAIL  //5
        );
        db.execSQL(createCartTable);
        db.execSQL(createTokenTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
