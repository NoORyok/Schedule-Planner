package schedulePlanner.org;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleDB extends SQLiteOpenHelper {

	public ScheduleDB(Context context) {
		super(context, "SCHEDULE_DB.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		// 테이블 생성(필드 : ID, 제목, 날짜, 시간, 메시지, 그룹명, 비밀번호)
		db.execSQL("CREATE TABLE IF NOT EXISTS SCHEDULE_TB(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "_TITLE TEXT, " + "_DATE TEXT , " + "_TIME TEXT, " + "_MSG TEXT, " + "_GROUP TEXT, " + "_PW TEXT );");

	}
	// 테이블 재생성
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub


		db.execSQL("DROP TABLE IF EXIST SCHEDULE_TB;");
		onCreate(db);
	}

}
