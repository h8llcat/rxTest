package sapotero.rxtest.db.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import timber.log.Timber;

final class DbOpenHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "esd.db";
  private static final int DATABASE_VERSION = 1;
  private static final String TAG = DbOpenHelper.class.getSimpleName();

  public DbOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    Timber.tag(TAG).v("constructor");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS RxAuth (  login TEXT PRIMARY KEY,  token TEXT,  collegue_login TEXT,  collegue_token TEXT)");
    db.execSQL("CREATE TABLE IF NOT EXISTS RxDocuments (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid TEXT, md5 TEXT, sort_key INTEGER, title TEXT, registration_number TEXT, registration_date DATE, urgency TEXT, short_description TEXT, comment TEXT, external_document_number TEXT, receipt_date DATE, signer_id INTEGER, viewed BOOLEAN)");
    db.execSQL("CREATE TABLE IF NOT EXISTS RxSigner (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, type TEXT, name TEXT, organisation TEXT, documents_id INTEGER)");
    db.execSQL("CREATE TABLE IF NOT EXISTS User (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, is_organization BOOLEAN, is_group BOOLEAN, name TEXT, organization TEXT, position TEXT, last_name TEXT, first_name TEXT, middle_name TEXT, gender TEXT, image TEXT)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Timber.tag(TAG).v("Update database from version  " + oldVersion + " to " + newVersion + ", which remove all old records");
    onCreate(db);
  }
}