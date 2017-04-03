package schedulePlanner.org;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener,
        OnItemClickListener {

    private ArrayList<String> monItems;
    private ArrayAdapter<String> monAdapter;

    private EditText editYear;
    private EditText editMon;
    private GridView calendarView;

    Button btnClick;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        editYear = (EditText) this.findViewById(R.id.editYear);
        editMon = (EditText) this.findViewById(R.id.editMon);

        monItems = new ArrayList<String>();
        monAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, monItems);

        calendarView = (GridView) this.findViewById(R.id.calendarView);     // 달력을 보여주는 그리드레이아웃
        calendarView.setAdapter(monAdapter);                                // Day 표시
        calendarView.setOnItemClickListener(this);                          // Day 클릭을 위한 이벤트

        Date date = new Date();
        int year = date.getYear() + 1900;
        int mon = date.getMonth() + 1;
        editYear.setText(year + "");
        editMon.setText(mon + "");

        fillDate(year, mon);

        btnClick = (Button) this.findViewById(R.id.btnClick);              // YYYY/DD
        btnClick.setOnClickListener(this);

    }

    // 날짜 선택
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        if (arg0.getId() == R.id.btnClick) {
            int year = Integer.parseInt(editYear.getText().toString());
            int mon = Integer.parseInt(editMon.getText().toString());
            fillDate(year, mon);
        }

    }

    // 날짜 표시
    private void fillDate(int year, int mon) {
        monItems.clear();
        monItems.add("일");
        monItems.add("월");
        monItems.add("화");
        monItems.add("수");
        monItems.add("목");
        monItems.add("금");
        monItems.add("토");

        Date current = new Date(year - 1900, mon - 1, 1);
        int day = current.getDay();

        // 공백 설정
        for (int i = 0; i < day; i++) {
            monItems.add("");
        }


        current.setDate(32);
        int last = 32 - current.getDate();

        // 날짜 채우기
        for (int i = 1; i <= last; i++) {
            monItems.add(i + "");
        }

        // 어댑터 갱신
        monAdapter.notifyDataSetChanged();

    }

    // DAYLIST 액티비티로 이동
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if (monItems.get(arg2).equals("")) {
            ;
        } else {

            //  호출한 액티비티에 날짜 값을 전달
            Intent intent = new Intent(this, Daylist.class);
            intent.putExtra("ParamDATE", editYear.getText().toString() + "/"
                    + editMon.getText().toString() + "/" + monItems.get(arg2));
            startActivity(intent);
        }
    }
}
