package com.example.printxpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "printxpress";
    private static final int DB_VER = 1;

    // customers table
    private static final String TABLE_CUSTOMERS = "customer";
    private static final String CUSTOMERS_COLUMN_ID = "id";
    private static final String CUSTOMERS_COLUMN_NAME = "name";
    private static final String CUSTOMERS_COLUMN_TYPE = "type";
    private static final String CUSTOMERS_COLUMN_EMAIL = "email";
    private static final String CUSTOMERS_COLUMN_PHONE = "phone";
    private static final String CUSTOMERS_COLUMN_PASSWORD = "password";

    // customer addresses table
    private static final String TABLE_ADDRESSES = "customer_address";
    private static final String ADDRESSES_COLUMN_CUSID = "cus_id";
    private static final String ADDRESSES_COLUMN_ID = "address_id";
    private static final String ADDRESSES_COLUMN_HOUSE_NO = "house_no";
    private static final String ADDRESSES_COLUMN_STREET = "street_name";
    private static final String ADDRESSES_COLUMN_SUBURB = "suburb";
    private static final String ADDRESSES_COLUMN_CITY = "city";

    // category table
    private static final String TABLE_CATEGORIES = "category";
    private static final String CATEGORIES_COLUMN_ID = "category_id";
    private static final String CATEGORIES_COLUMN_NAME = "name";

    // products table
    private static final String TABLE_PRODUCTS = "product";
    private static final String PRODUCTS_COLUMN_ID = "product_id";
    private static final String PRODUCTS_COLUMN_NAME = "name";
    private static final String PRODUCTS_COLUMN_DESC = "description";
    private static final String PRODUCTS_COLUMN_PRICE = "base_price";
    private static final String PRODUCTS_COLUMN_SAMPLE = "sample";
    private static final String PRODUCTS_COLUMN_CATEGORY_ID = "category_id";

    // specs table
    private static final String TABLE_SPECS = "specification";
    private static final String SPECS_COLUMN_ID = "spec_id";
    private static final String SPECS_COLUMN_NAME = "name";
    private static final String SPECS_COLUMN_VALUE = "value";

    // product specifications table
    private static final String TABLE_PRODUCT_SPEC = "product_spec";
    private static final String PRODUCT_SPEC_COLUMN_PRODUCT_ID = "product_id";
    private static final String PRODUCT_SPEC_COLUMN_SPEC_ID = "spec_id";

    // design table
    private static final String TABLE_DESIGNS = "design";
    private static final String DESIGNS_COLUMN_ID = "design_id";
    private static final String DESIGNS_COLUMN_TITLE = "title";
    private static final String DESIGNS_COLUMN_FILE = "file";
    private static final String DESIGNS_COLUMN_CUSTOMER_ID = "customer_id";

    // order table
    private static final String TABLE_ORDERS = "customer_order";
    private static final String ORDERS_COLUMN_ID = "order_id";
    private static final String ORDERS_COLUMN_DATE = "order_date";
    private static final String ORDERS_COLUMN_STATUS = "status";
    private static final String ORDERS_COLUMN_TOTAL = "total";
    private static final String ORDERS_COLUMN_CUSTOMER_ID = "customer_id";

    // order item table
    private static final String TABLE_ORDER_ITEMS = "order_item";
    private static final String ORDER_ITEMS_COLUMN_ORDER_ID = "order_id";
    private static final String ORDER_ITEMS_COLUMN_ID = "item_id";
    private static final String ORDER_ITEMS_COLUMN_CUSTOMTXT = "custom_txt";
    private static final String ORDER_ITEMS_COLUMN_PRICE = "unit_price";
    private static final String ORDER_ITEMS_COLUMN_QTY = "qty";
    private static final String ORDER_ITEMS_COLUMN_DESIGN_ID = "design_id";
    private static final String ORDER_ITEMS_COLUMN_PRODUCT_ID = "product_id";

    // order item spec table
    private static final String TABLE_ORDER_ITEM_SPECS = "order_item_spec";
    private static final String ORDER_ITEM_SPECS_ITEM_ID = "item_id";
    private static final String ORDER_ITEM_SPECS_SPEC_ID = "spec_id";

    // order delivery table
    private static final String TABLE_DELIVERIES = "order_delivery";
    private static final String DELIVERIES_COLUMN_ORDER_ID = "order_id";
    private static final String DELIVERIES_COLUMN_ID = "delivery_id";
    private static final String DELIVERIES_ADDRESS_ID = "address_id";
    private static final String DELIVERIES_COLUMN_DEL_OPT = "del_opt";
    private static final String DELIVERIES_COLUMN_DATETIME = "date_time";
    private static final String DELIVERIES_COLUMN_FEE = "del_fee";

    private static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + " (" +
            CUSTOMERS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CUSTOMERS_COLUMN_NAME + " TEXT NOT NULL, " +
            CUSTOMERS_COLUMN_TYPE + " TEXT NOT NULL, " +
            CUSTOMERS_COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
            CUSTOMERS_COLUMN_PHONE + " TEXT NOT NULL UNIQUE, " +
            CUSTOMERS_COLUMN_PASSWORD + " TEXT NOT NULL);";

    private static final String CREATE_ADDRESS_TABLE = "CREATE TABLE " + TABLE_ADDRESSES + " (" +
            ADDRESSES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ADDRESSES_COLUMN_CUSID + " INTEGER NOT NULL, " +
            ADDRESSES_COLUMN_HOUSE_NO + " TEXT NOT NULL, " +
            ADDRESSES_COLUMN_STREET + " TEXT NOT NULL, " +
            ADDRESSES_COLUMN_SUBURB + " TEXT NOT NULL, " +
            ADDRESSES_COLUMN_CITY + " TEXT NOT NULL, " +
            "FOREIGN KEY (" + ADDRESSES_COLUMN_CUSID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + CUSTOMERS_COLUMN_ID + "));";

    private static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            CATEGORIES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CATEGORIES_COLUMN_NAME + " TEXT NOT NULL UNIQUE);";

    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
            PRODUCTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PRODUCTS_COLUMN_NAME + " TEXT NOT NULL, " +
            PRODUCTS_COLUMN_DESC + " TEXT NOT NULL, " +
            PRODUCTS_COLUMN_PRICE + " REAL NOT NULL, " +
            PRODUCTS_COLUMN_SAMPLE + " TEXT NOT NULL, " +
            PRODUCTS_COLUMN_CATEGORY_ID + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + PRODUCTS_COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORIES_COLUMN_ID + "));";

    private static final String CREATE_SPEC_TABLE = "CREATE TABLE " + TABLE_SPECS + " (" +
            SPECS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SPECS_COLUMN_NAME + " TEXT NOT NULL, " +
            SPECS_COLUMN_VALUE + " TEXT NOT NULL);";

    private static final String CREATE_PRODUCT_SPEC_TABLE = "CREATE TABLE " + TABLE_PRODUCT_SPEC + " (" +
            PRODUCT_SPEC_COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
            PRODUCT_SPEC_COLUMN_SPEC_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + PRODUCT_SPEC_COLUMN_PRODUCT_ID + ", " + PRODUCT_SPEC_COLUMN_SPEC_ID + "), " +
            "FOREIGN KEY (" + PRODUCT_SPEC_COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + PRODUCTS_COLUMN_ID + "), " +
            "FOREIGN KEY (" + PRODUCT_SPEC_COLUMN_SPEC_ID + ") REFERENCES " + TABLE_SPECS + "(" + SPECS_COLUMN_ID + "));";

    private static final String CREATE_DESIGN_TABLE = "CREATE TABLE " + TABLE_DESIGNS + " (" +
            DESIGNS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DESIGNS_COLUMN_TITLE + " TEXT NOT NULL, " +
            DESIGNS_COLUMN_FILE + " TEXT NOT NULL, " +
            DESIGNS_COLUMN_CUSTOMER_ID + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + DESIGNS_COLUMN_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + CUSTOMERS_COLUMN_ID + "));";

    private static final String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDERS + " (" +
            ORDERS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDERS_COLUMN_DATE + " TEXT NOT NULL, " +
            ORDERS_COLUMN_STATUS + " TEXT NOT NULL, " +
            ORDERS_COLUMN_TOTAL + " REAL, " +
            ORDERS_COLUMN_CUSTOMER_ID + " INTEGER NOT NULL, " +
            "FOREIGN KEY (" + ORDERS_COLUMN_CUSTOMER_ID + ") REFERENCES " + TABLE_CUSTOMERS + "(" + CUSTOMERS_COLUMN_ID + "));";

    private static final String CREATE_ORDER_ITEM_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
            ORDER_ITEMS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDER_ITEMS_COLUMN_ORDER_ID + " INTEGER NOT NULL UNIQUE, " +
            ORDER_ITEMS_COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
            ORDER_ITEMS_COLUMN_DESIGN_ID + " INTEGER, " +
            ORDER_ITEMS_COLUMN_CUSTOMTXT + " TEXT, " +
            ORDER_ITEMS_COLUMN_QTY + " INTEGER NOT NULL, " +
            ORDER_ITEMS_COLUMN_PRICE + " REAL NOT NULL, " +
            "FOREIGN KEY (" + ORDER_ITEMS_COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + ORDERS_COLUMN_ID + "), " +
            "FOREIGN KEY (" + ORDER_ITEMS_COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + PRODUCTS_COLUMN_ID + "), " +
            "FOREIGN KEY (" + ORDER_ITEMS_COLUMN_DESIGN_ID + ") REFERENCES " + TABLE_DESIGNS + "(" + DESIGNS_COLUMN_ID + "));";

    private static final String CREATE_ORDER_ITEM_SPEC_TABLE = "CREATE TABLE " + TABLE_ORDER_ITEM_SPECS + " (" +
            ORDER_ITEM_SPECS_ITEM_ID + " INTEGER NOT NULL, " +
            ORDER_ITEM_SPECS_SPEC_ID + " INTEGER NOT NULL, " +
            "PRIMARY KEY (" + ORDER_ITEM_SPECS_ITEM_ID + ", " + ORDER_ITEM_SPECS_SPEC_ID + "), " +
            "FOREIGN KEY (" + ORDER_ITEM_SPECS_ITEM_ID + ") REFERENCES " + TABLE_ORDER_ITEMS + "(" + ORDER_ITEMS_COLUMN_ID + "), " +
            "FOREIGN KEY (" + ORDER_ITEM_SPECS_SPEC_ID + ") REFERENCES " + TABLE_SPECS + "(" + SPECS_COLUMN_ID + "));";

    private static final String CREATE_DELIVERY_TABLE = "CREATE TABLE " + TABLE_DELIVERIES + " (" +
            DELIVERIES_COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            DELIVERIES_COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
            DELIVERIES_ADDRESS_ID + " INTEGER, " +
            DELIVERIES_COLUMN_DEL_OPT + " TEXT NOT NULL, " +
            DELIVERIES_COLUMN_DATETIME + " TEXT NOT NULL, " +
            DELIVERIES_COLUMN_FEE + " REAL, " +
            "FOREIGN KEY (" + DELIVERIES_COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + ORDERS_COLUMN_ID + "), " +
            "FOREIGN KEY (" + DELIVERIES_ADDRESS_ID + ") REFERENCES " + TABLE_ADDRESSES + "(" + ADDRESSES_COLUMN_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // ensure FKs work properly to prevent orphan records
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_ADDRESS_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_SPEC_TABLE);
        db.execSQL(CREATE_PRODUCT_SPEC_TABLE);
        db.execSQL(CREATE_DESIGN_TABLE);
        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_ORDER_ITEM_TABLE);
        db.execSQL(CREATE_ORDER_ITEM_SPEC_TABLE);
        db.execSQL(CREATE_DELIVERY_TABLE);
        populateData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // dumping data to database
    private void populateData(SQLiteDatabase db) {
        // ==================== CATEGORIES ====================
        db.execSQL("INSERT INTO category (name) VALUES ('Business Cards')");   // 1
        db.execSQL("INSERT INTO category (name) VALUES ('Flyers')");           // 2
        db.execSQL("INSERT INTO category (name) VALUES ('Posters')");          // 3
        db.execSQL("INSERT INTO category (name) VALUES ('Banners')");          // 4
        db.execSQL("INSERT INTO category (name) VALUES ('Stickers')");         // 5
        db.execSQL("INSERT INTO category (name) VALUES ('Merchandise')");      // 6

        // ==================== PRODUCTS ====================
        // Business Cards
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Standard Business Card', 'Professional matte or glossy business cards', 1500.00, 'bizcard_standard.png', 1)");   // 1
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Premium Business Card', 'Thick luxury business cards with special finish', 2500.00, 'bizcard_premium.png', 1)");  // 2

        // Flyers
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('A4 Flyer', 'Full colour single or double sided A4 flyer', 2000.00, 'flyer_a4.png', 2)");   // 3
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('A5 Flyer', 'Compact A5 flyer for events and promotions', 1500.00, 'flyer_a5.png', 2)");   // 4

        // Posters
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('A3 Poster', 'Vibrant A3 poster print for indoor display', 3000.00, 'poster_a3.png', 3)");  // 5
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('A2 Poster', 'Large A2 poster for events and storefronts', 4500.00, 'poster_a2.png', 3)");  // 6

        // Banners
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Vinyl Banner', 'Durable outdoor vinyl banner with eyelets', 5000.00, 'banner_vinyl.png', 4)");      // 7
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Roll Up Banner', 'Portable retractable roll up display banner', 8500.00, 'banner_rollup.png', 4)"); // 8

        // Stickers
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Die Cut Stickers', 'Custom shaped stickers cut to your design outline', 800.00, 'sticker_diecut.png', 5)");        // 9
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Waterproof Vinyl Stickers', 'Durable waterproof stickers for outdoor use', 900.00, 'sticker_vinyl.png', 5)");     // 10

        // Merchandise
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Custom T-Shirt', 'High quality cotton t-shirt with custom print', 1800.00, 'merch_tshirt.png', 6)"); // 11
        db.execSQL("INSERT INTO product (name, description, base_price, sample, category_id) VALUES ('Custom Mug', 'Ceramic mug with full colour custom print', 1200.00, 'merch_mug.png', 6)");           // 12

        // ==================== SPECIFICATIONS ====================
        // Paper Type
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Paper Type', 'Matte')");    // 1
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Paper Type', 'Glossy')");   // 2
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Paper Type', 'Silk')");     // 3

        // Sides
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Sides', 'Single Sided')");  // 4
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Sides', 'Double Sided')");  // 5

        // Banner Size
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Banner Size', '2x4 ft')");  // 6
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Banner Size', '3x6 ft')");  // 7
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Banner Size', '4x8 ft')");  // 8

        // Sticker Size
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Sticker Size', '3x3 cm')");   // 9
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Sticker Size', '5x5 cm')");   // 10
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Sticker Size', '10x10 cm')"); // 11

        // Sticker Material
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Material', 'Vinyl')");  // 12
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Material', 'Paper')");  // 13

        // T-Shirt Size
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Size', 'S')");   // 14
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Size', 'M')");   // 15
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Size', 'L')");   // 16
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Size', 'XL')");  // 17

        // T-Shirt Color
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Color', 'White')");    // 18
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Color', 'Black')");    // 19
        db.execSQL("INSERT INTO specification (name, value) VALUES ('T-Shirt Color', 'Navy Blue')");// 20

        // Print Type
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Print Type', 'Screen Print')"); // 21
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Print Type', 'DTG Print')");    // 22

        // Mug Type
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Mug Type', 'White Ceramic')");        // 23
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Mug Type', 'Magic Colour Change')");  // 24

        // Mug Size
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Mug Size', '11oz')"); // 25
        db.execSQL("INSERT INTO specification (name, value) VALUES ('Mug Size', '15oz')"); // 26

        // ==================== PRODUCT SPECS ====================
        // Standard Business Card (1) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (1, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (1, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (1, 3)");
        db.execSQL("INSERT INTO product_spec VALUES (1, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (1, 5)");

        // Premium Business Card (2) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (2, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (2, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (2, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (2, 5)");

        // A4 Flyer (3) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (3, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (3, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (3, 3)");
        db.execSQL("INSERT INTO product_spec VALUES (3, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (3, 5)");

        // A5 Flyer (4) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (4, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (4, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (4, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (4, 5)");

        // A3 Poster (5) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (5, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (5, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (5, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (5, 5)");

        // A2 Poster (6) — paper type, sides
        db.execSQL("INSERT INTO product_spec VALUES (6, 1)");
        db.execSQL("INSERT INTO product_spec VALUES (6, 2)");
        db.execSQL("INSERT INTO product_spec VALUES (6, 4)");
        db.execSQL("INSERT INTO product_spec VALUES (6, 5)");

        // Vinyl Banner (7) — banner size
        db.execSQL("INSERT INTO product_spec VALUES (7, 6)");
        db.execSQL("INSERT INTO product_spec VALUES (7, 7)");
        db.execSQL("INSERT INTO product_spec VALUES (7, 8)");

        // Roll Up Banner (8) — banner size
        db.execSQL("INSERT INTO product_spec VALUES (8, 6)");
        db.execSQL("INSERT INTO product_spec VALUES (8, 7)");
        db.execSQL("INSERT INTO product_spec VALUES (8, 8)");

        // Die Cut Stickers (9) — size, material
        db.execSQL("INSERT INTO product_spec VALUES (9, 9)");
        db.execSQL("INSERT INTO product_spec VALUES (9, 10)");
        db.execSQL("INSERT INTO product_spec VALUES (9, 11)");
        db.execSQL("INSERT INTO product_spec VALUES (9, 12)");
        db.execSQL("INSERT INTO product_spec VALUES (9, 13)");

        // Waterproof Vinyl Stickers (10) — size, material
        db.execSQL("INSERT INTO product_spec VALUES (10, 9)");
        db.execSQL("INSERT INTO product_spec VALUES (10, 10)");
        db.execSQL("INSERT INTO product_spec VALUES (10, 11)");
        db.execSQL("INSERT INTO product_spec VALUES (10, 12)");

        // Custom T-Shirt (11) — size, color, print type
        db.execSQL("INSERT INTO product_spec VALUES (11, 14)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 15)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 16)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 17)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 18)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 19)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 20)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 21)");
        db.execSQL("INSERT INTO product_spec VALUES (11, 22)");

        // Custom Mug (12) — mug type, mug size
        db.execSQL("INSERT INTO product_spec VALUES (12, 23)");
        db.execSQL("INSERT INTO product_spec VALUES (12, 24)");
        db.execSQL("INSERT INTO product_spec VALUES (12, 25)");
        db.execSQL("INSERT INTO product_spec VALUES (12, 26)");
    }

    // CUSTOMER RELATED FUNCS
    public long insertCustomer(String name, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CUSTOMERS_COLUMN_NAME, name);
        values.put(CUSTOMERS_COLUMN_TYPE, "individual");
        values.put(CUSTOMERS_COLUMN_EMAIL, email);
        values.put(CUSTOMERS_COLUMN_PHONE, phone);
        values.put(CUSTOMERS_COLUMN_PASSWORD, password);
        return db.insert(TABLE_CUSTOMERS, null, values);
    }

    // check for duplicate email
    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CUSTOMERS_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_CUSTOMERS, new String[]{CUSTOMERS_COLUMN_ID}, selection,
                selectionArgs, null, null, null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // check for duplicate phone
    public boolean isPhoneRegistered(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CUSTOMERS_COLUMN_PHONE + " = ?";
        String[] selectionArgs = {phone};
        Cursor cursor = db.query(TABLE_CUSTOMERS, new String[]{CUSTOMERS_COLUMN_ID}, selection,
                selectionArgs, null, null, null
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // if the credentials passed to method are valid, return customer object
    public Customer getCustomerByCredential(String credential, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "(" + CUSTOMERS_COLUMN_EMAIL + " = ? OR " + CUSTOMERS_COLUMN_PHONE + " = ?) AND "
                + CUSTOMERS_COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {credential, credential, password};
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            Customer customer = new Customer(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_TYPE))
            );
            cursor.close();
            return customer;
        }
        cursor.close();
        return null;
    }

    // for showing profile info
    public Customer getCustomerInfo(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = CUSTOMERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(customerId)};
        Cursor cursor = db.query(TABLE_CUSTOMERS, null, selection, selectionArgs,
                null, null, null);
        if(cursor.moveToFirst()) {
            Customer customer = new Customer(
                    cursor.getInt(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_PASSWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CUSTOMERS_COLUMN_TYPE))
            );
            cursor.close();
            return customer;
        }
        cursor.close();
        return null;
    }

    // to updat ehe customer details
    public boolean updateCustomer(int id, String name, String type, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CUSTOMERS_COLUMN_NAME, name);
        values.put(CUSTOMERS_COLUMN_TYPE, type);
        values.put(CUSTOMERS_COLUMN_EMAIL, email);
        values.put(CUSTOMERS_COLUMN_PHONE, phone);
        values.put(CUSTOMERS_COLUMN_PASSWORD, password);

        try {
            int rowsAffected = db.update(TABLE_CUSTOMERS, values, CUSTOMERS_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            return rowsAffected > 0;
        } catch (SQLiteConstraintException e) { // since phone and email columns are UNIQUE, duplicate
            // registrations will throw constraint errors we handle them
            Log.e("DatabaseError", "Failed to update customer: " + e.getMessage());
            return false;
        }
    }

    // PRODUCT RELATED FUNCS
    // for displaying the products
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.*, c.name AS category_name FROM " + TABLE_PRODUCTS + " p " +
                "INNER JOIN " + TABLE_CATEGORIES + " c ON p.category_id = c.category_id";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_DESC)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_SAMPLE))
                );
                product.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow("category_name")));
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    // get the product info of specific productId ;
    // used in displaying ProductDetailsActivity
    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = PRODUCTS_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.query(TABLE_PRODUCTS, null, selection, selectionArgs, null, null, null);
        if(cursor.moveToFirst()) {
            Product product = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_DESC)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_COLUMN_SAMPLE))
            );
            cursor.close();
            return  product;
        }
        cursor.close();
        return null;
    }

    // to display the specifications associated with a product
    public List<Specification> getSpecsByProductId(int productId) {
        List<Specification> specList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT s.spec_id, s.name, s.value FROM " + TABLE_SPECS + " s " +
                      "INNER JOIN " + TABLE_PRODUCT_SPEC + " ps ON s.spec_id = ps.spec_id " +
                      "WHERE ps.product_id = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if(cursor.moveToFirst()) {
            do {
                Specification spec = new Specification(
                        cursor.getInt(cursor.getColumnIndexOrThrow(SPECS_COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SPECS_COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(SPECS_COLUMN_VALUE))
                );
                specList.add(spec);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return specList;
    }

    // ADDRESS RELATED FUNCS
    public long insertAddress(int customerId, String houseNo, String streetName, String suburb, String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ADDRESSES_COLUMN_CUSID, customerId);
        values.put(ADDRESSES_COLUMN_HOUSE_NO, houseNo);
        values.put(ADDRESSES_COLUMN_STREET, streetName);
        values.put(ADDRESSES_COLUMN_SUBURB, suburb);
        values.put(ADDRESSES_COLUMN_CITY, city);
        return db.insert(TABLE_ADDRESSES, null, values);
    }

    // to display list of customer addresses
    public List<Address> getCusAddresses(int customerId) {
        List<Address> addressList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ADDRESSES, null,
                ADDRESSES_COLUMN_CUSID + " = ?",
                new String[]{String.valueOf(customerId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Address address = new Address(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_CUSID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_HOUSE_NO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_STREET)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_SUBURB)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ADDRESSES_COLUMN_CITY))
                );
                addressList.add(address);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return addressList;
    }

    public boolean deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_ADDRESSES, ADDRESSES_COLUMN_ID + " = ?", new String[]{String.valueOf(addressId)});
            return rowsDeleted > 0;
        } catch (SQLiteConstraintException e) { // hanndle wheb ab address is linked to a order delivery
            Log.e("DatabaseError", "Cannot delete address — referenced by an existing order: " + e.getMessage());
            return false;
        }
    }

    // DESIGN RELATED FUNCS
    public long insertDesign(String title, String filePath, int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DESIGNS_COLUMN_TITLE, title);
        values.put(DESIGNS_COLUMN_FILE, filePath);
        values.put(DESIGNS_COLUMN_CUSTOMER_ID, customerId);
        return db.insert(TABLE_DESIGNS, null, values);
    }

    public List<Design> getCusDesigns(int customerId) {
        List<Design> designList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DESIGNS, null,
                DESIGNS_COLUMN_CUSTOMER_ID + " = ?", new String[]{String.valueOf(customerId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Design design = new Design(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_FILE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_CUSTOMER_ID))
                );
                designList.add(design);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return designList;
    }

    public Design getDesignById(int designId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = DESIGNS_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(designId)};
        Cursor cursor = db.query(TABLE_DESIGNS, null, selection,
                selectionArgs, null, null, null);
        if(cursor.moveToFirst()) {
            Design design = new Design(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DESIGNS_COLUMN_FILE))
            );
            cursor.close();
            return design;
        }
        cursor.close();
        return null;
    }

    public boolean deleteDesign(int designId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int rowsDeleted = db.delete(TABLE_DESIGNS, DESIGNS_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(designId)});
            return rowsDeleted > 0;
        } catch (SQLiteConstraintException e) {
            // if user attempts to delete a design that is used, FK constraint will be thrown so
            // we handle it by returning false : can be used to show toast
            // log it for reference
            Log.e("DatabaseError", "Cannot delete design bc its referenced by an " +
                    "existing order: " + e.getMessage());
            return false;
        }
    }

    // ORDER RELATED FUNCS
    public long insertOrder(int customerId, int productId, int designId, String customText, int qty, double unitPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        // first, insert the order itself
        ContentValues orderValues = new ContentValues();
        orderValues.put(ORDERS_COLUMN_DATE, String.valueOf(System.currentTimeMillis()));
        orderValues.put(ORDERS_COLUMN_STATUS, "processing");
        orderValues.put(ORDERS_COLUMN_TOTAL, unitPrice * qty);
        orderValues.put(ORDERS_COLUMN_CUSTOMER_ID, customerId);
        long orderId = db.insert(TABLE_ORDERS, null, orderValues);
        if (orderId == -1) return -1; // if orderId not inserted successfully, do not proceed just return -1

        // next, insert order item
        ContentValues itemValues = new ContentValues();
        itemValues.put(ORDER_ITEMS_COLUMN_ORDER_ID, orderId);
        itemValues.put(ORDER_ITEMS_COLUMN_PRODUCT_ID, productId);
        itemValues.put(ORDER_ITEMS_COLUMN_DESIGN_ID, designId == -1 ? null : designId);
        itemValues.put(ORDER_ITEMS_COLUMN_CUSTOMTXT, customText.isEmpty() ? null : customText);
        itemValues.put(ORDER_ITEMS_COLUMN_QTY, qty);
        itemValues.put(ORDER_ITEMS_COLUMN_PRICE, unitPrice);
        long itemId = db.insert(TABLE_ORDER_ITEMS, null, itemValues);
        if (itemId == -1) return -1; // stop here if item insert failed
        return itemId; // will need to insert specs
    }

    public boolean insertOrderItemSpecs(long itemId, List<Integer> specIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int specId : specIds) {
            ContentValues values = new ContentValues();
            values.put(ORDER_ITEM_SPECS_ITEM_ID, itemId);
            values.put(ORDER_ITEM_SPECS_SPEC_ID, specId);
            long result = db.insert(TABLE_ORDER_ITEM_SPECS, null, values);
            if(result==-1) return false;
        } return true;
    }

    public int getOrderIdByItemId(int itemId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = ORDER_ITEMS_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(itemId)};
        Cursor cursor = db.query(TABLE_ORDER_ITEMS, new String[]{ORDER_ITEMS_COLUMN_ORDER_ID},
                selection, selectionArgs, null, null, null);
        int orderId = -1;
        if(cursor.moveToFirst()) {
            orderId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ORDER_ITEMS_COLUMN_ORDER_ID)
            );
        }
        cursor.close();
        return orderId;
    }

    // DELIVER RELATED FUNCS
    public long insertDelivery(int orderId, int addressId, String deliveryOption,
                               String deliveryDateTime, double delFee) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DELIVERIES_COLUMN_ORDER_ID, orderId);
        if(addressId == -1){
            values.putNull(DELIVERIES_ADDRESS_ID);
        } else {
            values.put(DELIVERIES_ADDRESS_ID, addressId);
        }
        values.put(DELIVERIES_COLUMN_DEL_OPT, deliveryOption);
        values.put(DELIVERIES_COLUMN_DATETIME, deliveryDateTime);
        if(delFee == -1){
            values.putNull(DELIVERIES_COLUMN_FEE);
        } else {
            values.put(DELIVERIES_COLUMN_FEE, delFee);
        }
        return db.insert(TABLE_DELIVERIES, null, values);
    }

    // UPDATE FUNCS
    public boolean updateDeliveryDateTime(int orderId, String newDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DELIVERIES_COLUMN_DATETIME, newDateTime);
        int rowsAffected = db.update(TABLE_DELIVERIES, values,
                DELIVERIES_COLUMN_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)});
        return rowsAffected > 0;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORDERS_COLUMN_STATUS, newStatus);
        String selection = ORDERS_COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(orderId)};
        int rowsAffected = db.update(TABLE_ORDERS, values, selection, selectionArgs);
        return rowsAffected > 0;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ORDERS, null,
                ORDERS_COLUMN_ID + " = ?", new String[]{String.valueOf(orderId)},
                null, null, null);

        Order order = null;
        if (cursor.moveToFirst()) {
            order = new Order(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_COLUMN_CUSTOMER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_COLUMN_STATUS)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(ORDERS_COLUMN_TOTAL))

            );
        }
        cursor.close();
        return order;
    }

    // this function handles insertion of records to all tables related to a single order i.e.
    // order, order_item, order_item_spec, delivery by calling the responsible functions and using the
    // relevant objects
    public int insertCompleteOrder(int customerId, OrderItem orderItem, Delivery delivery, List<Integer> specIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            long itemId = insertOrder(customerId, orderItem.getProductId(), orderItem.getDesignId(),
                    orderItem.getCustomTxt(), orderItem.getQty(), orderItem.getUnitPrice());
            if (itemId == -1) throw new SQLException("Order/item insert failed");

            int orderId = getOrderIdByItemId((int) itemId);
            if (orderId == -1) throw new SQLException("Could not find order_id from item_id");

            boolean specsInserted = insertOrderItemSpecs(itemId, specIds);
            if (!specsInserted) throw new SQLException("Spec insert failed");

            long deliveryId = insertDelivery(orderId, delivery.getAddressId(), delivery.getDelOpt(),
                    delivery.getDateTime(), delivery.getDelFee());
            if (deliveryId == -1) throw new SQLException("Delivery insert failed");

            db.setTransactionSuccessful();
            return orderId;
        } catch (SQLException e) {
            Log.e("DatabaseError", "Complete order insertiion failed: " + e.getMessage());
            return -1;
        } finally {
            db.endTransaction();
        }
    }

    public List<HistoryHelper> getOrderHistoriesByCustomer(int customerId) {
        List<HistoryHelper> orderHistories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // querying for all the essential data reg each order so we can use in order history display
        String query = "SELECT o." + ORDERS_COLUMN_ID + " AS order_id, " +
                "o." + ORDERS_COLUMN_DATE + " AS order_date, " +
                "o." + ORDERS_COLUMN_STATUS + " AS status, " +
                "o." + ORDERS_COLUMN_TOTAL + " AS order_total, " +
                "p." + PRODUCTS_COLUMN_NAME + " AS product_name, " +
                "oi." + ORDER_ITEMS_COLUMN_QTY + " AS qty, " +
                "oi." + ORDER_ITEMS_COLUMN_CUSTOMTXT + " AS custom_txt, " +
                "d." + DESIGNS_COLUMN_TITLE + " AS design_title, " +
                "del." + DELIVERIES_COLUMN_DEL_OPT + " AS del_type, " +
                "del." + DELIVERIES_COLUMN_DATETIME + " AS del_datetime, " +
                "del." + DELIVERIES_COLUMN_FEE + " AS del_fee, " +
                "a." + ADDRESSES_COLUMN_HOUSE_NO + " AS house_no, " +
                "a." + ADDRESSES_COLUMN_STREET + " AS street_name, " +
                "a." + ADDRESSES_COLUMN_SUBURB + " AS suburb, " +
                "a." + ADDRESSES_COLUMN_CITY + " AS city " +
                "FROM " + TABLE_ORDERS + " o " +
                "INNER JOIN " + TABLE_ORDER_ITEMS + " oi ON o." + ORDERS_COLUMN_ID + " = oi." + ORDER_ITEMS_COLUMN_ORDER_ID + " " +
                "LEFT JOIN " + TABLE_PRODUCTS + " p ON oi." + ORDER_ITEMS_COLUMN_PRODUCT_ID + " = p." + PRODUCTS_COLUMN_ID + " " +
                "LEFT JOIN " + TABLE_DESIGNS + " d ON oi." + ORDER_ITEMS_COLUMN_DESIGN_ID + " = d." + DESIGNS_COLUMN_ID + " " +
                "LEFT JOIN " + TABLE_DELIVERIES + " del ON o." + ORDERS_COLUMN_ID + " = del." + DELIVERIES_COLUMN_ORDER_ID + " " +
                "LEFT JOIN " + TABLE_ADDRESSES + " a ON del." + DELIVERIES_ADDRESS_ID + " = a." + ADDRESSES_COLUMN_ID + " " +
                "WHERE o." + ORDERS_COLUMN_CUSTOMER_ID + " = ? " +
                "ORDER BY o." + ORDERS_COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(customerId)});

        if (cursor.moveToFirst()) {
            do {
                String customTxt = getNullableString(cursor, "custom_txt");
                String designTitle = getNullableString(cursor, "design_title");
                String delType = getNullableString(cursor, "del_type");
                String delDateTime = getNullableString(cursor, "del_datetime");
                int feeIndex = cursor.getColumnIndexOrThrow("del_fee");
                double delFee = cursor.isNull(feeIndex) ? 0 : cursor.getDouble(feeIndex);

                String addressDisplay = null;
                String houseNo = getNullableString(cursor, "house_no");
                if (houseNo != null) {
                    addressDisplay = houseNo + ", " +
                            getNullableString(cursor, "street_name") + ", " +
                            getNullableString(cursor, "suburb") + ", " +
                            getNullableString(cursor, "city");
                }

                HistoryHelper orderHistory = new HistoryHelper( // history helper object
                        cursor.getInt(cursor.getColumnIndexOrThrow("order_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("order_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status")),
                        cursor.getString(cursor.getColumnIndexOrThrow("product_name")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("qty")),
                        customTxt,
                        designTitle,
                        delType,
                        addressDisplay,
                        delDateTime,
                        cursor.getDouble(cursor.getColumnIndexOrThrow("order_total")),
                        delFee
                );
                orderHistories.add(orderHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderHistories;
    }

    // check for null
    private String getNullableString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        return cursor.isNull(index) ? null : cursor.getString(index);
    }
}


