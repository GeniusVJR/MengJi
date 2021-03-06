package com.geniusvjr.geniusnote.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.geniusvjr.geniusnote.R;
import com.geniusvjr.geniusnote.adapter.NoteAdapter;
import com.geniusvjr.geniusnote.constant.Constant;
import com.geniusvjr.geniusnote.db.NoteDB;
import com.geniusvjr.geniusnote.model.User;
import com.geniusvjr.geniusnote.view.CircleImageView;
import com.geniusvjr.geniusnote.widget.ModelPopup;

import java.util.Calendar;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;


public class NavigationViewActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String CONFIG_FIRST_START = "isFirstStart";

    private static final int REQUEST_CODE_ADD = 0;
    private static final int REQUEST_CODE_EDIT = 1;
    public static final int UPDATE_INFO = 2;


    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ListView mNoteListView;
    private FloatingActionButton btnAdd;
    private CircleImageView imgHead;

    private TextView userSays;
    private TextView tvUserName;
//    private ModelPopup mPopup;



    private NoteAdapter mNoteAdapter;
    private SharedPreferences sp;

    String userName;

    private int currentNavigationId;
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, Constant.APPID);
        setContentView(R.layout.activity_navigation_view);
        NoteDB.getInstance().open(this);
        onCheckFirstStart();
        sp = getSharedPreferences("UserInfo", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
//        SharedPreferences.Editor editor = sp.edit();


        initView();


        mNoteListView = (ListView) findViewById(R.id.NoteListView);
        mNoteAdapter = new NoteAdapter(this);
        mNoteListView.setAdapter(mNoteAdapter);
        registerForContextMenu(mNoteListView);
        AdapterView.OnItemLongClickListener longListener = new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mNoteListView.showContextMenu();
                return true;
            }

        };
        mNoteListView.setOnItemLongClickListener(longListener);
        mNoteListView.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sp.getBoolean("login", false)) {
//            BmobUser user = BmobUser.getCurrentUser(this);
//            String name = user.get();
//            User user = (User) User.getCurrentUser(this);
//            String name = user.getName();
//            tvUserName.setText(name);
        }
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.id_nv_menu);
        mNoteListView = (ListView) findViewById(R.id.NoteListView);
        View view = View.inflate(this, R.layout.header_just_username, null);
        View headerView = mNavigationView.getHeaderView(0);
        tvUserName = (TextView) headerView.findViewById(R.id.id_username);
        userSays = (TextView) headerView.findViewById(R.id.user_says);
        imgHead = (CircleImageView) headerView.findViewById(R.id.iv_head);
//        tvUserName.setText(userName);
        imgHead.setOnClickListener(this);
        mNoteAdapter = new NoteAdapter(this);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        setupDrawerContent(mNavigationView);
        AdapterView.OnItemLongClickListener longListener = new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                mNoteListView.showContextMenu();
                return true;
            }
        };
        mNoteListView.setOnItemLongClickListener(longListener);
        mNoteListView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        NoteDB.getInstance().close();
        super.onDestroy();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //改变item选中状态
                        menuItem.setChecked(true);
                        currentNavigationId = menuItem.getItemId();
                        switch (currentNavigationId) {
                            case R.id.nav_home:
                                startActivity(new Intent(NavigationViewActivity.this, WorldActivity.class));
                                break;
                            case R.id.about_us:
                                startActivity(new Intent(NavigationViewActivity.this, AboutUsActivity.class));
                                //设置切换动画，从右边进入，左边退出
//                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
//                                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                                break;
                            case R.id.settings:
                                startActivity(new Intent(NavigationViewActivity.this, UserCenterActivity.class));
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    protected void onCheckFirstStart() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mSharedPreferences.getBoolean(CONFIG_FIRST_START, true)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("# Markdown功能介绍\n\n");
        builder.append("本App支持一些简单的Markdown语法,您可以手动输入,也可以通过快捷工具栏来添加Markdown符号\n\n");
        builder.append("## **用法与规则**\n\n");
        builder.append("### **标题**\n");
        builder.append("使用\"#\"加空格在段首来创建标题\n\n");
        builder.append("例如:\n");
        builder.append("# 一级标题\n");
        builder.append("## 二级标题\n");
        builder.append("### 三级标题\n\n");
        builder.append("### **加粗功能**\n");
        builder.append("使用一组\"**\"来加粗一段文字\n\n");
        builder.append("例如:\n");
        builder.append("这是**加粗的文字**\n\n");
        builder.append("### **居中**\n");
        builder.append("使用一对大括号\"{}\"来居中一段文字(注:这是JNote特别添加的特性,非Markdown语法)\n\n");
        builder.append("例如:\n");
        builder.append("### {这是一个居中的标题}\n\n");
        builder.append("### **引用**\n");
        builder.append("使用\">\"在段首来创建引用\n\n");
        builder.append("例如:\n");
        builder.append("> 这是一段引用\n");
        builder.append("> 这是一段引用\n\n");
        builder.append("### **无序列表**\n");
        builder.append("使用\"-\"加空格在段首来创建无序列表\n\n");
        builder.append("例如:\n");
        builder.append("> 这是一个无序列表\n");
        builder.append("> 这是一个无序列表\n");
        builder.append("> 这是一个无序列表\n\n");
        builder.append("### **有序列表**\n");
        builder.append("使用数字圆点加空格在段首来创建有序列表\n\n");
        builder.append("例如:\n");
        builder.append("1. 这是一个有序列表\n");
        builder.append("2. 这是一个有序列表\n");
        builder.append("3. 这是一个有序列表\n\n");
        NoteDB.Note note = new NoteDB.Note();
        note.title = "Markdown功能介绍";
        note.content = builder.toString();
        note.date = Calendar.getInstance().getTimeInMillis();
        NoteDB.getInstance().insert(note);
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(CONFIG_FIRST_START, false);
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.data_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Intent intent = new Intent(NavigationViewActivity.this, NoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.iv_head:
                if (sp.getBoolean("login", false)) {

                    break;
                } else {
                    Intent intent1 = new Intent(this, LoginActivity.class);
                    startActivity(intent1);
                    break;
                }



        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.DataDelete:
                if (mSelectedPosition != -1) {
                    NoteDB.getInstance().delete(mSelectedPosition);
                    mNoteAdapter.notifyDataSetChanged();
                }
                return true;
            case R.id.DataClear:
                NoteDB.getInstance().clear();
                mNoteAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("NoteId", id);
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD || requestCode == REQUEST_CODE_EDIT) {
            mNoteAdapter.notifyDataSetChanged();
        }
//        if(requestCode == UPDATE_INFO)
//        {
//            tvUserName.setText("登录/注册");
//            userSays.setText("哈哈");
//        }

    }
}