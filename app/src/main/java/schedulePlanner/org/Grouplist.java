package schedulePlanner.org;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class Grouplist extends Activity {

    private final String SERVER_ADDRESS = "http://192.168.0.25:8080/SCHEDULE_SERVER";

    private EditText editSearch;
    private Button btnSearch;
    private ListView groupList;

    private ArrayList<String> groupItems;
    private ArrayAdapter<String> groupAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        // TODO Auto-generated method stub

        editSearch = (EditText) findViewById(R.id.editSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);

        groupList = (ListView) findViewById(R.id.groupList);
        groupItems = new ArrayList<String>();
        groupAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, groupItems);
        groupList.setAdapter(groupAdapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                // TODO Auto-generated method stub
                final Handler handler = new Handler();
                runOnUiThread(new Runnable() {

                    public void run() {
                        // TODO Auto-generated method stub
                        final ProgressDialog dialog = ProgressDialog.show(
                                Grouplist.this,
                                "불러오는중.....", "잠시만 기다려주세요");

                        handler.post(new Runnable() {

                            public void run() {
                                // TODO Auto-generated method stub
                                String gp = editSearch.getText().toString();
                                try {
                                    URL url = new URL(SERVER_ADDRESS + "/search.php?"
                                            + "_GROUP=" + URLEncoder.encode(gp, "UTF-8"));

                                    groupItems.clear();
                                    url.openStream();
                                    Log.e("SEARCH_LOG_URL", "" + url);

                                    ArrayList<String> title = getXmlTagList("search_result.xml", "_TITLE");
                                    ArrayList<String> time = getXmlTagList("search_result.xml", "_TIME");
                                    ArrayList<String> msg = getXmlTagList("search_result.xml", "_MSG");

                                    if (title.isEmpty())
                                        groupItems.add("아무것도 검색되지 않았습니다.");
                                    else {
                                        for (int i = 0; i < title.size(); i++) {
                                            String str = "        " + title.get(i) + "             " + time.get(i) + "             " + msg.get(i);
                                            groupItems.add(str);
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e("SEARCH_LOG_ERROR", e.getMessage());
                                } finally {
                                    dialog.dismiss();
                                    groupAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });

            }
        });
    }


    private ArrayList<String> getXmlTagList(String filename, String str) {
        String rss = SERVER_ADDRESS + "/";
        ArrayList<String> ret = new ArrayList<String>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            URL server = new URL(rss + filename);
            InputStream is = server.openStream();
            xpp.setInput(is, "UTF-8");

            Log.e("LIST_PARSING_LOG_URL", "" + server);
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals(str)) {
                        ret.add(xpp.nextText());
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            Log.e("LIST_PARSING_LOG_ERROR", e.getMessage());
        }

        return ret;
    }
}