package com.example.strivelifeapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.strivelifeapplication.ui.ChallengerItem;
import com.example.strivelifeapplication.ui.home.Task;
import com.example.strivelifeapplication.ui.notifications.ChallengerAdapter;
import com.example.strivelifeapplication.ui.notifications.ChallengerDecoration;
import com.example.strivelifeapplication.ui.notifications.MyAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class ContestDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private String title;
    String ParticipantName;
    int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest_detail);

        // 获取标题TextView并设置文本内容
        titleTextView = findViewById(R.id.textView_contest_name);
        title = getIntent().getStringExtra("title");
        Log.d("title",title);
        titleTextView.setText(title);

        // 声明并初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView_player);

        List<ChallengerItem> dataList = generateChallengerList();

        // 创建并设置适配器
        ChallengerAdapter adapter = new ChallengerAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // 设置布局管理器
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // 调整间隙
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new ChallengerDecoration(spacing));

        // 设置返回按钮的点击事件
        Button backButton = findViewById(R.id.button_new_player);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContestDetailActivity.this);
                builder.setTitle("輸入好友名稱");

                // 创建一个 EditText 作为对话框的内容
                final EditText inputEditText = new EditText(ContestDetailActivity.this);
                builder.setView(inputEditText);

                // 添加确认按钮
                builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userInput = inputEditText.getText().toString();
                        // 调用后端 API 建立ATTDENCE
                        AddAttendanceManager addAttendanceManager = new AddAttendanceManager();
                        String result = addAttendanceManager.add_Attendance(userInput, title, 0);
                        dataList.add(new ChallengerItem(userInput, 0));
                        adapter.updateData(dataList);
                        //!@#$
//                        result =  add_Attendance("sally", "再睡5分鐘", 0);
//
//
//                        // 新增好友到比賽中(IF條件為如果建立成功)
//                        if(result==成功) {
//                            dataList.add(new ChallengerItem(userInput, 0));
//                            adapter.updateData(dataList);
//                        }
                        //!@#$
                    }
                });

                // 添加取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel(); // 取消对话框
                    }
                });

                // 创建并显示对话框
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
    }


    // 要求後端生成參與比賽玩家数据列表的方法
    private List<ChallengerItem> generateChallengerList() {
        List<ChallengerItem> challengerItemList = new ArrayList<>();
        // 添加数据项到列表中
        GetContestParticipantManager ContestParticipantManager = new GetContestParticipantManager();
        JSONArray ParticipantData = ContestParticipantManager.getContestParticipant(title);
        for (int i = 0; i < ParticipantData.length(); i++) {
            // 获取当前数组元素（一个 JSONObject）
            JSONObject dataObject = null;
            try {
                dataObject = ParticipantData.getJSONObject(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // 从 dataObject 中获取 participant_name 和 score 值
            try {
                ParticipantName = dataObject.getString("participant_name");
                score = Integer.parseInt(dataObject.getString("score"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            ChallengerItem item = new ChallengerItem(ParticipantName, score);
            challengerItemList.add(item);

        }
//        !@#$
//        JSONObject dataObject = getContestParticipant(title);
//        把dataObject資料寫入challengerItemList
//        !@#$


//        challengerItemList.add(new ChallengerItem("雷諾哈特", 10));
//        challengerItemList.add(new ChallengerItem("萊西哈特", 15));
//        challengerItemList.add(new ChallengerItem("萊特哈特", 8));
//        challengerItemList.add(new ChallengerItem("梅露", 12));
//        challengerItemList.add(new ChallengerItem("赫夫", 5));
//        challengerItemList.add(new ChallengerItem("賽壬", 20));
        return challengerItemList;
    }

}
