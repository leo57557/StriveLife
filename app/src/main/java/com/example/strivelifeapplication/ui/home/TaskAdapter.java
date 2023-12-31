package com.example.strivelifeapplication.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

import androidx.lifecycle.ViewModelProvider;

import com.example.strivelifeapplication.MainActivity;
import com.example.strivelifeapplication.R;



// taskAdapter 協助連接後端 Task 資料和前端 Task 列表的介面
public class TaskAdapter extends BaseAdapter {

    // tasklist 儲存該使用者的所有 Task 資訊
    private ArrayList<Task> taskList;
    private final int VIEW_TYPE = 3;
    private final int TYPE_0 = 0;
    private final int TYPE_1 = 1;
    private Context mContext;
    private LayoutInflater inflater;
    private HomeViewModel homeViewModel;


    // 初始化
    public TaskAdapter(Context context, ArrayList<Task> taskList, HomeViewModel homeViewModel) {
        this.mContext = context;
        this.taskList = new ArrayList<Task>();
        this.taskList.addAll(taskList);
        inflater = LayoutInflater.from(mContext);
        this.homeViewModel = homeViewModel;
    }

    @Override
    public int getCount() {
        // TODO 自动生成的方法存根
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO 自动生成的方法存根
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO 自动生成的方法存根
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return VIEW_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        Task task = taskList.get(position);
        int type = task.getType();
        if(type == 0)
            return TYPE_0;
        else
            return TYPE_1;
    }

    // ViewHolder 管理 ListView 中的每個對象(Task) 及其屬性
    public class ViewHolder0 {
        TextView name;
        TextView condition;
        CheckBox checkbox;
    }

    public class ViewHolder1 {
        TextView name;
        TextView condition;
        EditText amount;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 宣告管理用的物件
        Task task = taskList.get(position);
//            int type = task.getType();
        int type = getItemViewType(position);
//            type = 0;
        ViewHolder0 holder0 = null;
        ViewHolder1 holder1 = null;
        Log.v("ConvertView", String.valueOf(convertView));
        Log.v("ConvertView", String.valueOf(type));


        // 當 convertView 為 null 時，表示没有可重複使用的視圖，也就是第一次呼叫到的對象
        // 將為該對象創建視圖及設置屬性

        if (convertView == null) {
            switch (type) {
                case TYPE_0:
                    convertView = inflater.inflate(R.layout.task0_info, parent,false);
                    holder0 = new ViewHolder0();
                    holder0.name = (TextView) convertView.findViewById(R.id.task0_name);
                    holder0.condition = (TextView) convertView.findViewById(R.id.task0_condition);
                    holder0.checkbox = (CheckBox) convertView.findViewById(R.id.task0_checkbox);
                    holder0.checkbox.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            CheckBox cb = (CheckBox) v;
                            Task task = (Task) cb.getTag();
                            Toast.makeText(mContext.getApplicationContext(),
                                    cb.getText() + " is " + cb.isChecked(),
                                    Toast.LENGTH_LONG).show();
                            task.setSelected(cb.isChecked());
                        }
                    });
                    convertView.setTag(holder0);
                    break;
                case TYPE_1:
                    convertView = inflater.inflate(R.layout.task1_info, parent,false);

                    holder1 = new ViewHolder1();
                    holder1.name = (TextView) convertView.findViewById(R.id.task1_name);
                    holder1.condition = (TextView) convertView.findViewById(R.id.task1_condition);
                    holder1.amount = (EditText) convertView.findViewById(R.id.task1_amount);

                    holder1.amount.addTextChangedListener(new TextWatcher() {
                        @Override  // 在文本变化之前执行的操作
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override  // 在文本变化时执行的操作
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override
                        public void afterTextChanged(Editable editable) {
                            int amount = Integer.parseInt(editable.toString());
                            task.setAmount(amount);
                            // 在文本变化之后执行的操作，你可以在此处获取输入完成后的文本
                            // 对获取到的文本进行处理
                        }
                    });
                    // 在文本編輯完成時觸發
                    holder1.amount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                // 在此處執行你想要的操作
                                // 例如：隱藏鍵盤
                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                                return true;
                            }
                            return false;
                        }
                    });

                    convertView.setTag(holder1);
                    break;
            }
        } else {
            // 有convertView，按样式，取得不用的布局
            switch (type) {
                case TYPE_0:
                    holder0 = (ViewHolder0) convertView.getTag();
                    break;
                case TYPE_1:
                    holder1 = (ViewHolder1) convertView.getTag();
                    break;
            }
        }

        // 设置资源
        switch (type) {
            case TYPE_0:
                holder0.name.setText(task.getName());
                holder0.condition.setText(" ( 有達成請勾選 )");
                holder0.checkbox.setChecked(task.isSelected());
                holder0.checkbox.setTag(task);
                break;
            case TYPE_1:
                holder1.name.setText(task.getName());
                holder1.condition.setText(" ( 預計達成量: " + task.getExpectedAmount() + ")");
                holder1.amount.setText(String.valueOf(task.getAmount()));
                holder1.amount.setTag(task);
                break;
        }
        return convertView;
    }
    public ArrayList<Task> getTaskList() {
        ArrayList<Task> taskList = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            taskList.add((Task) getItem(i));
        }
        return taskList;
    }
}


