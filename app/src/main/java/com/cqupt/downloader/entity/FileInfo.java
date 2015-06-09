package com.cqupt.downloader.entity;

import java.io.Serializable;

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
 * 描述：文件实体类，主要用来保存和获取文件信息，方便文件的操作
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ===================================================================
 */
public class FileInfo implements Serializable{

    private int id;
    private int finished;
    private String name;
    private String url;
    private int length;


    public FileInfo() {
    }

    public FileInfo(int id, int finished, String name, String url, int length) {
        this.id = id;
        this.finished = finished;
        this.name = name;
        this.url = url;
        this.length = length;
    }

//    public FileInfo(String name, String url, int length) {
//        this.name = name;
//        this.url = url;
//        this.length = length;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", length=" + length +
                '}';
    }
}
