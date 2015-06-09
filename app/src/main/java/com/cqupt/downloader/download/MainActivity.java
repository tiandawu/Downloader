package com.cqupt.downloader.download;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cqupt.downloader.entity.FileInfo;
import com.cqupt.downloader.service.DownloadService;
import com.cqupt.multidownload.activity.MultiDownlodActivity;


public class MainActivity extends Activity {

    private TextView text;
    private ProgressBar progress;
    private static final String PATH = "http://www.imooc.com/mobile/imooc.apk";
    private BroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(R.id.tv_text);
        progress = (ProgressBar) findViewById(R.id.pb_progress);


        progress.setMax(100);

        //����UI�Ĺ㲥������
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {

                    int finished = intent.getIntExtra("finished", 0);
//                    Log.i("完成量：", finished + "");
                    progress.setProgress(finished);
                    text.setText(finished+"%");
                    if(finished==100){
                        Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_LONG).show();
                    }

                }
            }
        };

        //ע��㲥
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void click(View v) {
        FileInfo fileInfo = new FileInfo(0,0,"imooc.apk", PATH, 6);
        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        switch (v.getId()) {
            case R.id.bt_download:
                Log.i("下载了吗？", "----------------------------------------");
                intent.setAction(DownloadService.ACTION_START);
                intent.putExtra(DownloadService.EXTRAL_FILE_INFO, fileInfo);
                startService(intent);

                break;

            case R.id.bt_pause:
                Log.i("暂停了吗？", "....................................");
                intent.setAction(DownloadService.ACTION_PAUSE);
                intent.putExtra(DownloadService.EXTRAL_FILE_INFO, fileInfo);
                startService(intent);
                break;
            case R.id.bt_skip:
                Log.i("跳转了吗？", "|||||||||||||||||||||||||||||||||||||||");
                Intent intent1 = new Intent(MainActivity.this, MultiDownlodActivity.class);
                startActivity(intent1);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
