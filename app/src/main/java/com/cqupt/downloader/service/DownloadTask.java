package com.cqupt.downloader.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cqupt.downloader.db.ThreadDAO;
import com.cqupt.downloader.db.ThreadDAOImpl;
import com.cqupt.downloader.entity.FileInfo;
import com.cqupt.downloader.entity.ThreadInfo;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DownloadTask {

    private Context context = null;
    private FileInfo mFileInfo = null;
    private ThreadDAO mThreadDAO = null;
    private int hasFinished = 0;
    public boolean isPause = false;
    private int mThreadcount = 1;
    private List<DownloadThread> mThreadList = null;
    public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

    public DownloadTask(Context context, FileInfo mFileInfo, int mThreadcount) {
        this.context = context;
        this.mThreadcount = mThreadcount;
        this.mFileInfo = mFileInfo;
        this.mThreadDAO = new ThreadDAOImpl(context);
    }

    public void download() {
        //��ȡ���ݿ��߳���Ϣ
        List<ThreadInfo> threadInfos = mThreadDAO.getThreads(mFileInfo.getUrl());
        if (threadInfos.size() == 0) {
            int length = mFileInfo.getLength() / mThreadcount;

            for (int i = 0; i < mThreadcount; i++) {
                ThreadInfo threadInfo = new ThreadInfo(i, i * length, (i + 1) * length - 1, 0, mFileInfo.getUrl());
                if (i == mThreadcount - 1) {
                    threadInfo.setEnd(mFileInfo.getLength());
                }

                threadInfos.add(threadInfo);

                mThreadDAO.insertThread(threadInfo);
            }

        }
        mThreadList = new ArrayList<>();
        //启动多个线程进行下载
        for (ThreadInfo thread : threadInfos) {
            DownloadThread downloadThread = new DownloadThread(thread);
//            downloadThread.start();
            DownloadTask.sExecutorService.execute(downloadThread);
            mThreadList.add(downloadThread);
        }
    }
//        new DownloadThread(threadInfo).start();

    /**
     * 判断是否所有线程下载完毕
     */
    private synchronized void checkAllThreadsFinished() {
        boolean allFinished = true;
        //遍历线程集合，看是否都下载完毕
        for (DownloadThread thread : mThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }

        if (allFinished) {
            //删除下载记录
            mThreadDAO.deleteThread(mFileInfo.getUrl());
            //发送广播通知UI下载任务结束
            Intent intent = new Intent(DownloadService.ACTION_FINISHED);
            intent.putExtra("fileInfo", mFileInfo);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 数据下载线程
     */
    private class DownloadThread extends Thread {
        private ThreadInfo threadInfo = null;
        public boolean isFinished = false;//表示线程是否执行完毕

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {


            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(threadInfo.getPath());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                int start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                //�����ļ�д��λ��
                File file = new File(DownloadService.DOWNLOD_PATH, mFileInfo.getName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                hasFinished += threadInfo.getFinished();
                //��ʼ����
                if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    //��ȡ����
                    inputStream = conn.getInputStream();
                    byte[] buff = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buff)) != -1) {
                        //д���ļ�
                        raf.write(buff, 0, len);
                        //�����ؽ��ȷ��͹㲥��Activity
                        hasFinished += len;
                        //累加每个线程完成的进度
                        threadInfo.setFinished(threadInfo.getFinished() + len);
                        if (System.currentTimeMillis() - time > 1000) {
                            time = System.currentTimeMillis();
                        intent.putExtra("finished", hasFinished * 100 / mFileInfo.getLength());
                        intent.putExtra("fileId", mFileInfo.getId());
                        context.sendBroadcast(intent);
//                        Log.i("我发送了广播", "????????????????????????");
                        }

                        //��������ͣʱ���������ؽ��ȵ����ݿ�
                        if (isPause) {
                            mThreadDAO.updateThread(threadInfo.getPath(), threadInfo.getId(), threadInfo.getFinished());
                            return;
                        }

                    }

                }
                //标示线程下载完毕
                isFinished = true;
                //检查下载任务是否执行完毕
                checkAllThreadsFinished();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    raf.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}

