package com.cqupt.downloader.db;

import com.cqupt.downloader.entity.ThreadInfo;

import java.util.List;




/**
 * ���ݿ���ʽӿ�
 */
public interface ThreadDAO {

    /**
     * �����߳���Ϣ
     */
    void insertThread(ThreadInfo threadInfo);

    /**
     * ɾ���߳���Ϣ
     */
    void deleteThread(String url);

    /**
     * �����߳�������Ϣ
     */
    void updateThread(String url, int thread_id, int finished);

    /**
     * ��ѯ�ļ��̵߳���Ϣ
     */
    List<ThreadInfo> getThreads(String url);

    /**
     * ��ѯ�߳��Ƿ����
     */
    boolean isExists(String url, int thread_id);
}
