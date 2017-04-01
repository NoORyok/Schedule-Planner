package schedulePlanner.org;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

public class Crud extends Activity implements OnClickListener {

    // 외부 주소(포트포워딩)  "http://27.124.225.117/SCHEDULE_SERVER";
    // 내부 주소(포트:8080)  "http://192.168.0.xx:8080/SCHEDULE_SERVER";
    private final String SERVER_ADDRESS = "http://192.168.0.25:8080/SCHEDULE_SERVER";

    private ScheduleDB scheduleDB;                              // DB CREATE
    private SQLiteDatabase sqlDB;                               // DB SQL

    private int mId;                                            // 호출 아이템 ID
    private int sendpoint, updatepoint;                         // CRUD를 제어하기 위한 변수
    private String strDay2;                                     // 날짜 문자열

    private EditText editDate, editTitle, editTime, editMsg, editPw, editGroup;
    private Button btnSave, btnDelete, btnCancel, btnUpdate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        editDate = (EditText) findViewById(R.id.editDate);
        editTitle = (EditText) findViewById(R.id.editTitle);
        editTime = (EditText) findViewById(R.id.editTime);
        editMsg = (EditText) findViewById(R.id.editMsg);
        editGroup = (EditText) findViewById(R.id.editGroup);
        editPw = (EditText) findViewById(R.id.editPw);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);


        Intent intent = getIntent();
        mId = intent.getIntExtra("ParamID", -1);                        // 일정 ID 값 반환
        strDay2 = intent.getStringExtra("ParamDATE");                   // 일정 날짜 값 반환

        scheduleDB = new ScheduleDB(this);  // DB 불러오기

        // 일정이 없는 경우
        if (mId == -1) {

            btnDelete.setVisibility(View.GONE);                         // 삭제버튼 비활성화
            btnUpdate.setVisibility(View.GONE);                         // 수정버튼 비활성화

            editDate.setText(strDay2);                                  // 날짜 설정

        } else {

            btnSave.setVisibility(View.GONE);                           // 저장버튼 비활성화

            // DB 에서 ID 검색
            sqlDB = scheduleDB.getWritableDatabase();
            Cursor cursor = sqlDB.rawQuery("SELECT * FROM SCHEDULE_TB WHERE _ID='" + mId
                    + "'", null);

            if (cursor.moveToNext()) {

                editTitle.setText(cursor.getString(1));
                editDate.setText(cursor.getString(2));
                editTime.setText(cursor.getString(3));
                editMsg.setText(cursor.getString(4));
                editGroup.setText(cursor.getString(5));
                editPw.setText(cursor.getString(6));
            }
            cursor.close();
            scheduleDB.close();
        }

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        sqlDB = scheduleDB.getWritableDatabase();

        switch (v.getId()) {

            case R.id.btnSave:
                sendpoint = 0;

                // 일정 값이 비어있는 경우
                if (editTitle.getText().toString().equals("") ||
                        editTime.getText().toString().equals("") ||
                        editMsg.getText().toString().equals("") ||
                        editPw.getText().toString().equals("") ||
                        editGroup.getText().toString().equals("")) {

                    Toast.makeText(Crud.this, "일정을 입력하시오", Toast.LENGTH_SHORT).show();
                    return;
                }

                // DB에 일정 저장
                sqlDB.execSQL("INSERT INTO SCHEDULE_TB VALUES(null, '"
                        + editTitle.getText().toString() + "', '"
                        + editDate.getText().toString() + "', '"
                        + editTime.getText().toString() + "', '"
                        + editMsg.getText().toString() + "', '"
                        + editGroup.getText().toString() + "', '"
                        + editPw.getText().toString() + "');");



                sendpoint = 1;

                scheduleDB.close();
                setResult(RESULT_OK);


                if (sendpoint == 1)
                {
                    String title1 = editTitle.getText().toString();
                    String date1 = strDay2.toString();
                    String time1 = editTime.getText().toString();
                    String msg1 = editMsg.getText().toString();
                    String group1 = editGroup.getText().toString();
                    String pw1 = editPw.getText().toString();


                    // 서버에 일정 정보를 GET 방식으로 전송
                    try {
                        Log.e("INSERT_LOG_DATE", date1);
                        URL url = new URL(SERVER_ADDRESS + "/insert.php?"
                                + "_TITLE=" + URLEncoder.encode(title1, "UTF-8")
                                + "&_DATE=" + URLEncoder.encode(date1, "UTF-8")
                                + "&_TIME=" + URLEncoder.encode(time1, "UTF-8")
                                + "&_MSG=" + URLEncoder.encode(msg1, "UTF-8")
                                + "&_GROUP=" + URLEncoder.encode(group1, "UTF-8")
                                + "&_PW=" + URLEncoder.encode(pw1, "UTF-8"));
                        url.openStream();

                        Log.e("INSERT_LOG_URL", "" + url);

                        // XML에 저장된 result 값을 파싱하여 반환
                        String result = getXmlTag("insert_result.xml", "result");

                        if (result.equals("1")) { // 서버 통신이 성공함


                            Toast.makeText(Crud.this, " 일정 추가 성공", Toast.LENGTH_SHORT).show();

                            // 초기화
                            editTitle.setText("");
                            editDate.setText("");
                            editTime.setText("");
                            editMsg.setText("");
                            editGroup.setText("");
                            editPw.setText("");

                            sendpoint = 0;

                        } else
                            Toast.makeText(Crud.this, " 일정 추가 실패", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.e("INSERT_LOG_ERORR", e.getMessage());
                    }

                }
                break;

            case R.id.btnDelete:

                String title2 = editTitle.getText().toString();
                String date2 = editDate.getText().toString();
                String time2 = editTime.getText().toString();
                String msg2 = editMsg.getText().toString();
                String pw2 = editPw.getText().toString();
                String group2 = editGroup.getText().toString();

                try {

                    URL url = new URL(SERVER_ADDRESS + "/delete.php?"
                            + "_TITLE=" + URLEncoder.encode(title2, "UTF-8")
                            + "&_DATE=" + URLEncoder.encode(date2, "UTF-8")
                            + "&_TIME=" + URLEncoder.encode(time2, "UTF-8")
                            + "&_MSG=" + URLEncoder.encode(msg2, "UTF-8")
                            + "&_GROUP=" + URLEncoder.encode(group2, "UTF-8")
                            + "&_PW=" + URLEncoder.encode(pw2, "UTF-8"));
                    url.openStream();


                    String result = getXmlTag("delete_result.xml", "result");

                    if (result.equals("1")) {
                        Toast.makeText(Crud.this, " 일정 삭제 성공", Toast.LENGTH_SHORT).show();

                        editTitle.setText("");
                        editDate.setText("");
                        editTime.setText("");
                        editMsg.setText("");
                        editPw.setText("");
                        editGroup.setText("");



                    } else
                        Toast.makeText(Crud.this, "일정 삭제 실패", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e("DELETE_LOG_ERROR", e.getMessage());
                }

                // 저장된 일정 삭제
                if (mId != -1) {
                    sqlDB.execSQL("DELETE FROM SCHEDULE_TB WHERE _ID='" + mId + "';");
                    scheduleDB.close();
                }

                setResult(RESULT_OK);
                break;

            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                break;

            case R.id.btnUpdate:
                updatepoint = 0;

                // 지정된 일정 정보를 수정함
                if (mId != -1) {
                    sqlDB.execSQL("UPDATE SCHEDULE_TB SET _TITLE='"
                            + editTitle.getText().toString() + "', _DATE='"
                            + editDate.getText().toString() + "', _TIME='"
                            + editTime.getText().toString() + "', _MSG='"
                            + editMsg.getText().toString() + "', _GROUP='"
                            + editGroup.getText().toString() + "', _PW='"
                            + editPw.getText().toString() + "' WHERE _ID='" + mId
                            + "';");

                    updatepoint = 1;
                }

                scheduleDB.close();
                setResult(RESULT_OK);

                if (updatepoint == 1) {
                    String title3 = editTitle.getText().toString();
                    String date3 = editDate.getText().toString();
                    String time3 = editTime.getText().toString();
                    String msg3 = editMsg.getText().toString();
                    String group3 = editGroup.getText().toString();
                    String pw3 = editPw.getText().toString();


                    try {
                        Log.e("UPDATE_LOG_DATE", date3);
                        URL url = new URL(SERVER_ADDRESS + "/update.php?"
                                + "_TITLE=" + URLEncoder.encode(title3, "UTF-8")
                                + "&_DATE=" + URLEncoder.encode(date3, "UTF-8")
                                + "&_TIME=" + URLEncoder.encode(time3, "UTF-8")
                                + "&_MSG=" + URLEncoder.encode(msg3, "UTF-8")
                                + "&_GROUP=" + URLEncoder.encode(group3, "UTF-8")
                                + "&_PW=" + URLEncoder.encode(pw3, "UTF-8"));

                        url.openStream();
                        Log.e("UPDATE_LOG_URL", "" + url);


                        String result = getXmlTag("update_result.xml", "result");

                        if (result.equals("1")) {
                            Toast.makeText(Crud.this, " 일정 수정 성공", Toast.LENGTH_SHORT).show();

                            editTitle.setText("");
                            editDate.setText("");
                            editTime.setText("");
                            editMsg.setText("");
                            editPw.setText("");
                            editGroup.setText("");

                            updatepoint = 0;

                        } else
                            Toast.makeText(Crud.this, " 일정 수정 실패", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Log.e("UPDATE_LOG_ERROR", e.getMessage());
                    }

                }
                break;
        }
        finish();
    }

    // XML 태그값을 파싱하기 위한 메서드
    private String getXmlTag(String filename, String str) {
        String rss = SERVER_ADDRESS + "/";
        String ret = "";

        //XML 파싱
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); // PULLParserFactory
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();        // PullParser 파서
            URL server = new URL(rss + filename);               // XML 불러오기
            InputStream is = server.openStream();               // XML 문서 파싱.
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();                // 파서로부터 데이터를 반환

            //XML 코드 끝으로 이동
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG) {     // 요소의 시작태그를 만났을 때

                    if (xpp.getName().equals(str)) {            // 태그명이 str 문자열과 동일함
                        ret = xpp.nextText();                   // 파싱 값 저장
                    }
                }

                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e("PARSING_LOG_ERROR", e.getMessage());
        }

        return ret;
    }

}
