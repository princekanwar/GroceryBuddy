    package com.darshil.grocerybuddy.Database;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    import java.io.FileInputStream;

    public class AccountDB extends SQLiteOpenHelper {
        private Context context;
        private static String dbName = "Login";
        private static String tableName = "profile";
        private static String idColumn = "id";
        private static String UsernameColumn = "username";
        private static String PasswordColumn = "password";
        private static String firstNameColumn = "fname";
        private static String lastNameColumn = "lname";
        private static String emailColumn = "email";
        private static String contactColumn = "contact";
        private static String imageColumn = "image";

        public AccountDB(Context context)
        {
            super(context,dbName,null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL( "create table "+tableName+"(" +idColumn+" integer primary key autoincrement, "+
            UsernameColumn+" text, "+
                    PasswordColumn+" text, "+
                    firstNameColumn+" text, "+
                    lastNameColumn+" text, "+
                    emailColumn+" text, "+
                    contactColumn+" text)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL( "drop table if exists "+tableName );
            onCreate( db );
            }

            public boolean create(Account account)
            {
                boolean result=true;
                try{
                    SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put( UsernameColumn,account.getUsername() );
                    contentValues.put( PasswordColumn,account.getPassword() );
                    contentValues.put( firstNameColumn,account.getFirstName() );
                    contentValues.put( lastNameColumn,account.getLastName() );
                    contentValues.put( emailColumn,account.getEmail() );
                    contentValues.put(contactColumn,account.getContact() );
                    result = sqLiteDatabase.insert( tableName,null,contentValues ) > 0;

                }catch (Exception e){
                    result = false;
                }
                return result;
            }
        public boolean update(Account account)
        {
            boolean result=true;
            try{
                SQLiteDatabase sqLiteDatabase = getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put( UsernameColumn,account.getUsername() );
                contentValues.put( PasswordColumn,account.getPassword() );
                contentValues.put( firstNameColumn,account.getFirstName() );
                contentValues.put( lastNameColumn,account.getLastName() );
                contentValues.put( emailColumn,account.getEmail() );
                contentValues.put(contactColumn,account.getContact() );
                result = sqLiteDatabase.update( tableName, contentValues, idColumn + " = ?",
                        new String[] {String.valueOf( account.getId() )} ) > 0;
            }catch (Exception e){
                result = false;
            }
            return result;
        }
            public Account login(String username,String password)
            {
                Account account = null;
                try {
                    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
                    Cursor cursor = sqLiteDatabase.rawQuery( "select * from " + tableName + " where username = ? and password =  ?", new String[]{username, password} );
                    if (cursor.moveToFirst()) {
                        account = new Account();
                        account.setId(cursor.getInt( 0 ));
                        account.setUsername(cursor.getString( 1 ));
                        account.setPassword(cursor.getString( 2 ));
                        account.setFirstName(cursor.getString( 3 ));
                        account.setLastName(cursor.getString( 4 ));
                        account.setEmail(cursor.getString( 5 ));
                        account.setContact(cursor.getString( 6 ));
                    }
                }catch(Exception e)
                {
                    account = null;
                }
                return account;
            }

        public Account checkUsername(String username)
        {
            Account account = null;
            try {
                SQLiteDatabase sqLiteDatabase = getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery( "select * from " + tableName + " where username = ?", new String[]{username } );
                if (cursor.moveToFirst()) {
                    account = new Account();
                    account.setId(cursor.getInt( 0 ));
                    account.setUsername(cursor.getString( 1 ));
                    account.setPassword(cursor.getString( 2 ));
                    account.setFirstName(cursor.getString( 3 ));
                    account.setLastName(cursor.getString( 4 ));
                    account.setEmail(cursor.getString( 5 ));
                    account.setContact(cursor.getString( 6 ));
                }
            }catch(Exception e)
            {
                account = null;
            }
            return account;
        }
        public Account find(int id)
        {
            Account account = null;
            try {
                SQLiteDatabase sqLiteDatabase = getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery( "select * from " + tableName + " where id = ?",
                        new String[] {String.valueOf( id ) } );
                if (cursor.moveToFirst()) {
                    account = new Account();
                    account.setId(cursor.getInt( 0 ));
                    account.setUsername(cursor.getString( 1 ));
                    account.setPassword(cursor.getString( 2 ));
                    account.setFirstName(cursor.getString( 3 ));
                    account.setLastName(cursor.getString( 4 ));
                    account.setEmail(cursor.getString( 5 ));
                    account.setContact(cursor.getString( 6 ));
                }
            }catch(Exception e)
            {
                account = null;
            }
            return account;
        }
    }
