package com.cqupt.downloader.entity;

/**
 * ===================================================================
 * <p/>
 * 版权：重庆邮电大学教育集团 版权所有 （c）  2015
 * <p/>
 * 作者：田大武
 * <p/>
 * 版本：1.0
 * <p/>
 * 创建日期：${date} ${time}
 * <p/>
 * 描述：线程的实体类，主要用来保存和获取线程的信息，方便在数据库中存取等操作
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ===================================================================
 */
public class ThreadInfo {

    private int id;
    private int start;
    private int end;
    private int finished;
    private String path;

    public ThreadInfo() {
    }

    public ThreadInfo(int id, int start, int end, int finished, String path) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.finished = finished;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                ", path='" + path + '\'' +
                '}';
    }
}
