package com.cqupt.multidownload.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cqupt.downloader.download.MainActivity;
import com.cqupt.downloader.download.R;
import com.cqupt.downloader.entity.FileInfo;
import com.cqupt.downloader.service.DownloadService;
import com.cqupt.multidownload.FileListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MultiDownlodActivity extends Activity {
    private ListView listView;
    private List<FileInfo> mFileInfo;
    private BroadcastReceiver mBroadReceiver = null;
    private FileListAdapter fla = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_downlod);
        listView = (ListView) findViewById(R.id.lv_list);



        mFileInfo = new ArrayList<>();
        FileInfo fileInfo = new FileInfo(0, 0, "imoock", "http://www.imooc.com/mobile/imooc.apk", 0);
        FileInfo fileInfo1 = new FileInfo(1, 0, "baidu", "http://file.liqucn.com/upload/2011/qita_shenghuo/bdmobile.android.app_3.0.0.4_liqucn.com.apk", 0);
        FileInfo fileInfo2 = new FileInfo(2, 0, "tenxun", "http://file.liqucn.com/upload/2014/xinwen/com.tencent.news_4.6.7_liqucn.com.apk", 0);
        FileInfo fileInfo3 = new FileInfo(3, 0, "gaode", "http://file.liqucn.com/upload/2011/gps/com.autonavi.minimap_7.3.0.2036_liqucn.com.apk", 0);
        FileInfo fileInfo4 = new FileInfo(4, 0, "weidian", "http://file.liqucn.com/upload/2014/gouwu/com.koudai.weishop_5.3.5_liqucn.com.apk", 0);
        mFileInfo.add(fileInfo);
        mFileInfo.add(fileInfo1);
        mFileInfo.add(fileInfo2);
        mFileInfo.add(fileInfo3);
        mFileInfo.add(fileInfo4);


        fla = new FileListAdapter(MultiDownlodActivity.this, mFileInfo);
        listView.setAdapter(fla);


        mBroadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                    int finished = intent.getIntExtra("finished", 0);
                    int fileId = intent.getIntExtra("fileId", 0);
                    fla.updateProgress(fileId, finished);
                } else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())) {
                    FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                    fla.updateProgress(fileInfo.getId(), 0);
                    Toast.makeText(MultiDownlodActivity.this, mFileInfo.get(fileInfo.getId()).getName() + "下载完毕！", Toast.LENGTH_SHORT).show();
                }
            }
        };


        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        filter.addAction(DownloadService.ACTION_FINISHED);
        registerReceiver(mBroadReceiver, filter);
    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.bt_skip:
                Intent intent = new Intent(MultiDownlodActivity.this, MultiDownlodActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadReceiver);
    }
}
