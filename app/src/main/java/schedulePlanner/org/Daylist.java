package schedulePlanner.org;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Daylist extends Activity implements OnItemClickListener,
        OnClickListener {

    private ScheduleDB scheduleDB;                      // DB OPEN 객체
    private SQLiteDatabase sqlDB;                       // DB SQL 객체

    private String strDay;                             // 날짜 문자열

    private Cursor cursor;                              // DB 커서
    private SimpleCursorAdapter scheduleAdapter;        // 일정 표시 ADAPTER
    private ListView scheduleList;                      // 일정 리스트뷰
    private TextView textDay;                           // 날짜 표시
    private Button btnInsert, btnGroup;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extoday);

        Intent intent = getIntent();
        strDay = intent.getStringExtra("ParamDATE");           // 날짜 값을 받음

        textDay = (TextView) findViewById(R.id.textDay);
        textDay.setText(strDay);

        scheduleDB = new ScheduleDB(this);                      // DB OPEN
        sqlDB = scheduleDB.getWritableDatabase();               // DB SQL

        // 선택한 날짜의 CULUMN 반환
        cursor = sqlDB.rawQuery("SELECT * FROM SCHEDULE_TB WHERE _DATE = '" + strDay + "'", null);


        // 선택한 날짜의 일정들을 표시
        scheduleAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, new String[]{
                "_TITLE", "_TIME"}, new int[]{android.R.id.text1,
                android.R.id.text2});

        scheduleList = (ListView) findViewById(R.id.scheduleList);
        scheduleList.setAdapter(scheduleAdapter);
        scheduleList.setOnItemClickListener(this);

        scheduleDB.close();

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(this);

        btnGroup = (Button) findViewById(R.id.btnGroup);
        btnGroup.setOnClickListener(this);

    }

    // 저장된 일정을 선택
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(this, Crud.class);
        cursor.moveToPosition(position);                    // 선택한 일정으로 커서를 이동
        intent.putExtra("ParamID", cursor.getInt(0));       // ID 값 전달
        startActivityForResult(intent, 0);                  // 양방향 인텐트
    }

    // DB CRUD
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            // 일정추가 버튼
            case R.id.btnInsert:
                Intent intent1 = new Intent(this, Crud.class);
                intent1.putExtra("ParamDATE", strDay);             // 날짜 값 전달
                startActivityForResult(intent1, 1);
                break;

            // 그룹일정 버튼
            case R.id.btnGroup:
                Intent intent2 = new Intent(getApplicationContext(), Grouplist.class);
                startActivity(intent2);
                break;
        }

    }

    // 호출한 액티비티가 종료되어 되돌아와 실행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
            case 1:
                if (resultCode == RESULT_OK) {            // CRUD 작업 후 반환된 resultCode 값 비교

                    // 해당 날짜의 DB 일정을 호출
                    sqlDB = scheduleDB.getWritableDatabase();
                    cursor = sqlDB.rawQuery("SELECT * FROM SCHEDULE_TB WHERE _DATE = '"
                            + strDay + "'", null);
                    scheduleAdapter.changeCursor(cursor);

                    scheduleDB.close();
                }
                break;
        }
    }
}
