package com.example.strivelifeapplication.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.app.AlertDialog;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.content.Context;
import android.util.Log;




import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.strivelifeapplication.GetAttendedContestManager;
import com.example.strivelifeapplication.MainActivity;
import com.example.strivelifeapplication.UpdateavatarManager;
import com.example.strivelifeapplication.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import com.example.strivelifeapplication.R;
import com.example.strivelifeapplication.ui.dashboard.Friend;
import com.example.strivelifeapplication.ui.dashboard.FriendAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    ArrayList<Task> taskList = new ArrayList<>();
    TaskAdapter taskAdapter = null;
    private ListView settingsListView;
    private boolean isSettingsVisible = false;
    private List<Integer> imageList;
    private AlertDialog imagePickerDialog;
    private ImageView selectedImageView;
    HomeViewModel homeViewModel;
    View root;
    boolean init_flag = true;
    ImageView avatar;  // Replace with your ImageView reference
    String Name;
    String contestName;
    int contestType;
    String deadline;
    int expected_amount;
    int award_score;
    String award_avatar;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);


        settingsListView = root.findViewById(R.id.settingsListView);
        ImageButton settingsButton = root.findViewById(R.id.settings_button);

        imageList = new ArrayList<>();
        imageList.add(AvatarNameToId("water"));
        imageList.add(AvatarNameToId("sleep"));
        imageList.add(AvatarNameToId("muscle"));
        imageList.add(AvatarNameToId("hero"));


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSettingsVisible) {
                    hideSettings();
                } else {
                    showSettings();
                }
            }
        });

        ListView listView = root.findViewById(R.id.listView1);

        if (init_flag){
            homeViewModel.setMyName("marow");
            Name = homeViewModel.getMyName();
            GetAttendedContestManager AttendedContestManager = new GetAttendedContestManager();
            JSONArray FriendData = AttendedContestManager.getAttendedContest(Name);

            for (int i = 0; i < FriendData.length(); i++) {
                // 获取当前数组元素（一个 JSONObject）
                JSONObject dataObject = null;
                try {
                    dataObject = FriendData.getJSONObject(i);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // 从 dataObject 中获取 participant_name 和 score 值
                String ContestName = null;
                try {
                    contestName = dataObject.getString("contest_name");
                    contestType = Integer.parseInt(dataObject.getString("type"));
                    expected_amount = Integer.parseInt(dataObject.getString("expected_amount"));
                    Task task = new Task(contestName, contestType, false, expected_amount, null);
                    taskList.add(task);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            homeViewModel.setAvatarName("strivelife");
            init_flag = false;
        }
        else {
            taskList = homeViewModel.getTaskList();
            avatar = root.findViewById(R.id.avatar);
            avatar.setImageResource(AvatarNameToId(homeViewModel.getAvatarName()));
            Log.d("好累", homeViewModel.getAvatarName());
        }


        taskAdapter = new TaskAdapter(requireContext(), taskList, homeViewModel);
        listView.setAdapter(taskAdapter);

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (taskAdapter != null) {
            ArrayList<Task> updatedTaskList = taskAdapter.getTaskList();
            homeViewModel.updataTaskList(updatedTaskList);
        }
        binding = null;
    }

    private void showSettings() {
        settingsListView.setVisibility(View.VISIBLE);
        isSettingsVisible = true;

        ArrayList<String> settingsData = new ArrayList<String>();
//        settingsData.add("更換稱號");
        settingsData.add("更換頭像");


        // 創建您的設定列表適配器，例如 ArrayAdapter 或自定義的適配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, settingsData);

        // 設定設定列表的適配器
        settingsListView.setAdapter(adapter);
        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取点击的项的位置position，并根据需要执行相应的事件
                switch (position) {
                    case 0:
                        openChangeAvatarDialog();
                        break;
                }
            }
        });

    }

    private void hideSettings() {
        settingsListView.setVisibility(View.GONE);
        isSettingsVisible = false;
    }

    private void openChangeAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_avatar_window, null);
        builder.setView(dialogView);
        imagePickerDialog = builder.create();
        selectedImageView = root.findViewById(R.id.avatar);

        GridView gridView = dialogView.findViewById(R.id.gridView);
        ImageAdapter adapter = new ImageAdapter(requireContext(), imageList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedImageResId = imageList.get(position);
                selectedImageView.setImageResource(selectedImageResId);

                // 儲存avatar圖片的名稱到HomeViewModel
                String avatarName = AvatarIdToName(selectedImageResId);
                homeViewModel.setAvatarName(avatarName);

                // updata avatar 到 database
                UpdateavatarManager updateavatarManager = new UpdateavatarManager();
                updateavatarManager.updateAvatar(Name, avatarName);
                imagePickerDialog.dismiss();
            }
        });

        imagePickerDialog.show();
    }
    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private List<Integer> imageList;

        public ImageAdapter(Context context, List<Integer> imageList) {
            this.context = context;
            this.imageList = imageList;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Integer getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(imageList.get(position));

            return imageView;
        }
    }
    public int AvatarNameToId(String Name) {
        // Get the resource ID of the drawable using its name
        int resourceId = getResources().getIdentifier(Name, "drawable", requireContext().getPackageName());
        return resourceId;
    }
    private String AvatarIdToName(int resourceId) {
        // Get the resource name of the drawable using its ID
        String resourceName = getResources().getResourceEntryName(resourceId);
        return resourceName;
    }
}