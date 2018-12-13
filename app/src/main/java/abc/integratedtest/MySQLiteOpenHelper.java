package abc.integratedtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HAN on 2017-10-08.
 */

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) { // Context, 파일명, CursorFactory, 버전
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { // DB 생성(DB 없을 시에만 호출)
        sqLiteDatabase.execSQL("create table memo( " +
                "_id integer primary key autoincrement, " +
                "content text, " +
                "feel_id integer, " +
                "weather_id integer, " +
                "date_year integer, " +
                "date_month integer, " +
                "date_day integer);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { // 버전 변경 시 테이블 삭제 및 재 생성 (DB, 현버전, 새버전)
        sqLiteDatabase.execSQL("drop table if exists memo");

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists memo");

        onCreate(db);
    }
}
