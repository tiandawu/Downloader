package com.cqupt.multidownload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cqupt.downloader.download.R;
import com.cqupt.downloader.entity.FileInfo;
import com.cqupt.downloader.service.DownloadService;

import java.util.List;

/**
 * ===================================================================
 * <p/>
 * 版权：重庆邮电大学教育集团 版权所有 （c）  2015
 * <p/>
 * 作者：小甜甜
 * <p/>
 * 版本：1.0
 * <p/>
 * 创建日期：${date} ${time}
 * <p/>
 * 描述：
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ===================================================================
 */
public class FileListAdapter extends BaseAdapter {

    private Context context;
    private List<FileInfo> mFilelist ;

    public FileListAdapter(Context context, List<FileInfo> mFilelist) {
        this.context = context;
        this.mFilelist = mFilelist;
    }

    @Override
    public int getCount() {
        return mFilelist.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FileInfo fileInfo = mFilelist.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            //加载视图
            convertView = LayoutInflater.from(context).inflate(R.layout.listitem, null);
            //获得布局中的控件
            holder = new ViewHolder();
            holder.tvFile = (TextView) convertView.findViewById(R.id.tv_showtext);
            holder.pbFile = (ProgressBar) convertView.findViewById(R.id.mpb_progress);
            holder.btStart = (Button) convertView.findViewById(R.id.mbt_download);
            holder.btStop = (Button) convertView.findViewById(R.id.mbt_pause);
            holder.tvFile.setText(fileInfo.getName());
            holder.pbFile.setMax(100);
            holder.btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通知service开始下载
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_START);
                    intent.putExtra(DownloadService.EXTRAL_FILE_INFO, fileInfo);
                    context.startService(intent);
                }
            });

            holder.btStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //通知service停止下载
                    Intent intent = new Intent(context, DownloadService.class);
                    intent.setAction(DownloadService.ACTION_PAUSE);
                    intent.putExtra(DownloadService.EXTRAL_FILE_INFO, fileInfo);
                    context.startService(intent);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置视图中的控件
        holder.pbFile.setProgress(fileInfo.getFinished());
        return convertView;
    }

    /**
     * 更新列表中的进度条
     */
    public void updateProgress(int id,int progress) {

        FileInfo fileInfo = mFilelist.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }
    private static class ViewHolder{
        TextView tvFile;
        ProgressBar pbFile;
        Button btStart;
        Button btStop;
    }
}
