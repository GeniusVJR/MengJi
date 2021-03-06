package com.geniusvjr.geniusnote.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geniusvjr.geniusnote.R;
import com.geniusvjr.geniusnote.model.User;
import com.geniusvjr.tclibrary.activities.SlidingFinishActionBarActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by dream on 16/2/1.
 */
public class UpdateUsernameActivity extends SlidingFinishActionBarActivity implements View.OnClickListener {

    private EditText activity_updateProfile_profile;
    private Button activity_updateProfile_confirm;

    private User curUser;

    public static final int MINE_INFO_FINISH_FIND_USER = 1;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MINE_INFO_FINISH_FIND_USER:
                    initView();
                    break;
                default:
                    break;
            }
        }


    };

    private void updateData() {
        BmobUser bmobUser = BmobUser.getCurrentUser(this);
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("objectId", bmobUser.getObjectId());
        query.findObjects(this, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                curUser = list.get(0);
                Message msg = new Message();
                msg.what = MINE_INFO_FINISH_FIND_USER;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(int i, String s) {
                toast("获取当前用户失败");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_username);
        updateData();

    }

    private void initView() {
        activity_updateProfile_profile = (EditText) findViewById(R.id.activity_updateProfile_profile);
        activity_updateProfile_confirm = (Button) findViewById(R.id.activity_updateProfile_confirm);
        activity_updateProfile_confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.activity_updateProfile_confirm:
                String userName = activity_updateProfile_profile.getText().toString();
                curUser.setName(userName);
                curUser.update(this, curUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        finish();
                        Toast.makeText(UpdateUsernameActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(UpdateUsernameActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }


    private void toast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }
}
