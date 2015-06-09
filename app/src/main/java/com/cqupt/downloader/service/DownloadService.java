package com.cqupt.downloader.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cqupt.downloader.entity.FileInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownloadService extends Service {

    public static final String DOWNLOD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mDownload/";
    private static final String TAG = "MYTag";
    public static final String EXTRAL_FILE_INFO = "file_info";
    public static final String ACTION_START = "action_start";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_FINISHED = "action_finished";
    public static final String ACTION_UPDATE = "action_update";
    private static final int MSG_INIT = 1;
    //下载任务的集合
    private Map<Integer, DownloadTask> mTasks = new LinkedHashMap<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_INIT) {
                FileInfo fileInfo = (FileInfo) msg.obj;
                Log.i(TAG, "文件信息：" + fileInfo);
                DownloadTask task = new DownloadTask(DownloadService.this, fileInfo, 3);
                task.download();
                mTasks.put(fileInfo.getId(), task);
            }
        }
    };


    public DownloadService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("intnt为null吗？", intent + "   ??????");

        if (intent == null) {
            Log.i(TAG, "intent= null");
            return super.onStartCommand(intent, flags, startId);
        }

        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra(EXTRAL_FILE_INFO);
            Log.i(TAG, "开始：  " + fileInfo.toString());
            Log.i(TAG, "路径： = " + DOWNLOD_PATH);

            //启动初始化线程
//            new InitThread(fileInfo).start();
            DownloadTask.sExecutorService.execute(new InitThread(fileInfo));

        } else if (ACTION_PAUSE.equals(action)) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra(EXTRAL_FILE_INFO);
            Log.i(TAG, "暂停： " + fileInfo.toString());
            DownloadTask task = mTasks.get(fileInfo.getId());
            if (task != null) {
                task.isPause = true;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /*
    初始化子线程
     */
    private class InitThread extends Thread {

        private FileInfo mFileInfo;

        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {

            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            //获取连接
            try {
                Log.i("URL没找到吗？", mFileInfo.getUrl()+"是的啊！！！！！！！！！！！！！");
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpStatus.SC_OK) {
                    //获取文件长度
                    length = conn.getContentLength();
                }

                if (length <= 0) {
                    return;
                }

                //在本地创建一个与服务器文件相同大小的文件
                File dir = new File(DOWNLOD_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                File file = new File(dir, mFileInfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                //设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
                handler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }

                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
