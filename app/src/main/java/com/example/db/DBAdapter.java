package com.example.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import android.database.sqlite.SQLiteException;

public class DBAdapter extends SQLiteOpenHelper {

	private static String DB_PATH = "";
	private static final String DB_NAME = "BRTS_DB.sqlite";
	public SQLiteDatabase myDataBase;
	private static Context myContext;
	private static DBAdapter mDBConnection;
	public static int count = 0;
	public static int file = 0;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */

	private static void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[2048];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	@SuppressLint("SdCardPath")
	@SuppressWarnings({ "static-access" })
	private DBAdapter(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;

		DB_PATH = "/data/data/"
				+ context.getApplicationContext().getPackageName()
				+ "/databases/";

		System.out.println("DB path is" + DB_PATH);
		// The Android's default system path of your application database is
		// "/data/data/mypackagename/databases/"
	}

	/**
	 * getting Instance
	 * 
	 * @param context
	 * @return DBAdapter
	 */
	public static synchronized DBAdapter getDBAdapterInstance(Context context) {
		if (mDBConnection == null) {
			// copyDataBase();
			mDBConnection = new DBAdapter(context);
		}
		return mDBConnection;
	}

	/**
	 * Creates an empty database on the system and rewrites it with your own
	 * database.
	 **/
	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
			// copyDataBase();

		} else {
			// By calling following method
			// 1) an empty database will be created into the default system path
			// of your application
			// 2) than we overwrite that database with our database.
			this.getReadableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */

	public boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	/*
	 * Privious Code private boolean checkDataBase() { SQLiteDatabase checkDB =
	 * null; try{ String myPath = DB_PATH + DB_NAME; checkDB =
	 * SQLiteDatabase.openDatabase(myPath, null,
	 * SQLiteDatabase.NO_LOCALIZED_COLLATORS |
	 * SQLiteDatabase.CREATE_IF_NECESSARY);
	 * 
	 * }catch (SQLiteException e){ // database does't exist yet. } if (checkDB
	 * != null) { checkDB.close(); } return checkDB != null ? true : false; }
	 */

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */

	/**
	 * Open the database
	 * 
	 * @throws SQLException
	 */
	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	/**
	 * Close the database if exist
	 */
	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	/**
	 * Call on creating data base for example for creating tables at run time
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/**
	 * can used for drop tables then call onCreate(db) function to create tables
	 * again - upgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// ----------------------- CRUD Functions ------------------------------

	/*
	 * public Cursor selectRecordsFromDB(String tableName, String[]
	 * tableColumns, String whereClase, String whereArgs[], String groupBy,
	 * String having, String orderBy) { return myDataBase.query(tableName,
	 * tableColumns, whereClase, whereArgs, groupBy, having, orderBy); }
	 */

	public ArrayList<ArrayList<String>> selectRecordsFromDBList(
			String tableName, String[] tableColumns, String whereClase,
			String whereArgs[], String groupBy, String having, String orderBy) {

		ArrayList<ArrayList<String>> retList = new ArrayList<ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		Cursor cursor = myDataBase.query(tableName, tableColumns, whereClase,
				whereArgs, groupBy, having, orderBy);
		if (cursor.moveToFirst()) {
			do {
				list = new ArrayList<String>();
				for (int i = 0; i < cursor.getColumnCount(); i++) {
					list.add(cursor.getString(i));
				}
				retList.add(list);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return retList;

	}

	public long insertRecordsInDB(String tableName, String nullColumnHack,
			ContentValues initialValues) {
		return myDataBase.insert(tableName, nullColumnHack, initialValues);
	}

	public void exactQuery(StringBuilder sql) {
		try {

			/*
			 * String extStorageDirectory = Environment
			 * .getExternalStorageDirectory().toString(); File folder = new
			 * File(extStorageDirectory, "SQLs"); if(!folder.exists())
			 * folder.mkdir();
			 * 
			 * FileOutputStream fop = null; File file; String content =
			 * sql.toString();
			 * 
			 * try { file = new
			 * File(folder.getAbsolutePath()+"/Query_"+DBAdapter.file+".txt");
			 * fop = new FileOutputStream(file);
			 * 
			 * // if file doesnt exists, then create it if (!file.exists()) {
			 * file.createNewFile(); }
			 * 
			 * // get the content in bytes byte[] contentInBytes =
			 * content.getBytes();
			 * 
			 * fop.write(contentInBytes); fop.flush(); fop.close();
			 * 
			 * System.out.println("Done"); DBAdapter.file++; } catch
			 * (IOException e1) { e1.printStackTrace(); } finally { try { if
			 * (fop != null) { fop.close(); } } catch (IOException e2) {
			 * e2.printStackTrace(); } }
			 */
			myDataBase.execSQL(sql.toString());
		} catch (Exception e) {
			/*
			 * String extStorageDirectory = Environment
			 * .getExternalStorageDirectory().toString(); File folder = new
			 * File(extStorageDirectory, "ERROR"); if(!folder.exists())
			 * folder.mkdir();
			 * 
			 * FileOutputStream fop = null; File file; String content =
			 * e.toString()+
			 * "\n-------------------------------------------------------------------------------------------------------------------------------------------------------\n\n"
			 * +sql.toString();
			 * 
			 * try { file = new
			 * File(folder.getAbsolutePath()+"/ErrorFile_"+DBAdapter
			 * .file+".txt"); fop = new FileOutputStream(file);
			 * 
			 * // if file doesnt exists, then create it if (!file.exists()) {
			 * file.createNewFile(); }
			 * 
			 * // get the content in bytes byte[] contentInBytes =
			 * content.getBytes();
			 * 
			 * fop.write(contentInBytes); fop.flush(); fop.close();
			 * 
			 * System.out.println("Done"); DBAdapter.file++; } catch
			 * (IOException e1) { e1.printStackTrace(); } finally { try { if
			 * (fop != null) { fop.close(); } } catch (IOException e2) {
			 * e2.printStackTrace(); } }
			 */
		}
	}

	public void DeleteAll(String sql) {
		myDataBase.execSQL(sql);
	}

	public boolean updateRecordInDB(String tableName,
			ContentValues initialValues, String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs) > 0;
	}

	public int updateRecordsInDB(String tableName, ContentValues initialValues,
			String whereClause, String whereArgs[]) {
		return myDataBase.update(tableName, initialValues, whereClause,
				whereArgs);
	}

	public int deleteRecordInDB(String tableName, String whereClause,
			String[] whereArgs) {
		return myDataBase.delete(tableName, whereClause, whereArgs);
	}

	// --------------------- Select Raw Query Functions ---------------------

	public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
		return myDataBase.rawQuery(query, selectionArgs);
	}

	public void Run_Exact_Query(String s) {
		myDataBase.execSQL(s);
	}

	public Cursor selectquery(String s) {
		return myDataBase.rawQuery(s, null);
	}

}
