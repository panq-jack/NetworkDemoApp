package com.pq.networkdemoapp.normal;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pq.networkdemoapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by panqian on 2018/4/24
 * description:
 */

public class NormalActivity extends AppCompatActivity {

    Spinner spinner;
    ImageView imageView;
    TextView textView;

    StringBuilder logBuilder=new StringBuilder();
    Map<String ,String> cacheMap=new HashMap<>();
    String filesPath;
    boolean firstAccess=true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filesPath =getExternalFilesDir("normalPicCache").toString();
        Log.d("ppp","filesPath  "+ filesPath);

        setContentView(R.layout.activity_normal);
        spinner=(Spinner)findViewById(R.id.spinner);
        imageView=(ImageView)findViewById(R.id.image);
        textView=(TextView)findViewById(R.id.tv_log);
        textView.setMovementMethod(new ScrollingMovementMethod());
        initSpinner();

    }
    private void initSpinner(){
        final List<String> urls=new ArrayList<>();
        urls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1525255639&di=caaf4bf01a6348e715cd98efdcca2286&imgtype=jpg&er=1&src=http%3A%2F%2Fpic31.nipic.com%2F20130806%2F3347542_160328960000_2.jpg");
        urls.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=983787674,1712447029&fm=27&gp=0.jpg");
        urls.add("https://img14.360buyimg.com/da/jfs/t18151/181/711963467/356465/61cb4330/5aa0c546Nfa8c4ff0.jpg!cr_1125x690_0_72.webp");
        urls.add("https://m.360buyimg.com/mobilecms/s1080x527_jfs/t19579/284/1758616054/204651/f0990504/5ad6fd22N0bb2a855.jpg!cr_1125x690_0_72!q70.jpg.webp");
        urls.add("https://img1.360buyimg.com/da/jfs/t17230/183/1965973333/44074/109b0669/5add98f5Nbbe4a8b3.jpg!cr_1125x690_0_72.webp");
        urls.add("https://m.360buyimg.com/mobilecms/s640x1136_jfs/t17833/91/1804676202/107490/bece2f59/5ad89377Nafa273e4.jpg!q70.jpg.webp");
        SpinnerAdapter adapter=new SpinnerAdapter();
        adapter.setData(urls);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(NormalActivity.this,"onItemSelected pos: "+position,Toast.LENGTH_SHORT).show();

                String url=urls.get(position);
                if (!TextUtils.isEmpty(url)){
                    showTipDialog(url,!firstAccess);
                }
                if (firstAccess){
                    firstAccess=false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(NormalActivity.this,"onNothingSelected",Toast.LENGTH_SHORT).show();
            }
        });

        initCacheMap(urls);

    }

    private void tryToShowImageView(String url){
        //清空
        logBuilder.delete(0,logBuilder.length());
        updateLog("log1:   [start] load picUrl  :"+url);
        //有缓存
        if (cacheMap != null && cacheMap.containsKey(url)){
            String cacheFile=cacheMap.get(url);
            updateLog("log2:   [memory cache hit]  fileCache  :"+cacheFile);
            //缓存文件存在
            if (checkFileExists(cacheFile)){
                updateLog("log3:   [file cache hit]  fileCache exists  :"+cacheFile);
                Drawable drawable=Drawable.createFromPath(cacheFile);
                imageView.setImageDrawable(drawable);
                updateLog("log4:   [show image]  show image from cache  ");
                return;
            }
        }
        new DownLoadFileTask().execute(url);
    }

    class SpinnerAdapter extends BaseAdapter{

        private List<String> lists;
        public void setData(List<String> _lists){
            this.lists=_lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler hodler = null;
            if (convertView == null) {
                hodler = new ViewHodler();
                TextView textView = new TextView(NormalActivity.this);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);
                textView.setPadding(10,5,10,5);
                textView.setBackgroundColor(Color.argb(50,50,50,50));
                textView.setMaxLines(1);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                convertView=textView;
                hodler.mTextView = (TextView) convertView;
                convertView.setTag(hodler);
            } else {
                hodler = (ViewHodler) convertView.getTag();
            }

            hodler.mTextView.setText(lists.get(position));

            return convertView;
        }

        private  class ViewHodler{
            TextView mTextView;
        }
    }

    private void showTipDialog(final String url,boolean showTip){
        if (!showTip){
            tryToShowImageView(url);
        }else {
            AlertDialog tipDialog=new AlertDialog.Builder(this)
                    .setTitle("确定下载吗")
                    .setMessage("图片地址：  "+url)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tryToShowImageView(url);
                        }
                    })
                    .create();
            tipDialog.show();
        }
    }


    class DownLoadFileTask extends AsyncTask<String,Integer,Integer>{
        long totalProgress=0l;
        File file;
        String url;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            updateLog("log5:   [memory cache miss]  start get image from network :");
        }

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                url=strings[0];
                HttpURLConnection httpURLConnection=(HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream=httpURLConnection.getInputStream();

                //生成文件
                String fileName=getFilenameForKey(url);
                File dir=new File(filesPath);
                if (!dir.exists()){
                    updateLog("log6:   [create cache dir ]  dir :"+dir);
                    dir.mkdirs();
                }

                file=new File(dir,fileName);
                //创建失败会抛异常
                if (file.createNewFile()){
                    updateLog("log7:   [create cacheFile  ]  file :"+file);
                }

                OutputStream outputStream=new FileOutputStream(file);
                byte[] buffer = new byte[4*1024];
                int length;
                while ((length=inputStream.read(buffer))!=-1){
                    publishProgress(length);
                    outputStream.write(buffer);
                }
                publishProgress(-1);
                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress=values[0];
            if (-1==progress){
                updateLog("log8:   [downloading  complete & write to file]  "  +"|totalProgress:  "+totalProgress);
            }else {
                totalProgress+=progress;
                updateLog("log8:   [downloading  ]  progress :"+progress+"  |totalProgress:  "+totalProgress);
            }

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer > 0){
                updateLog("log9:   [show image]  show image from newwork  ");
                Drawable drawable=Drawable.createFromPath(file.getPath());
                imageView.setImageDrawable(drawable);
                updateLog("log9:   [cache url]  write url to cache map ");
                cacheMap.put(url,file.getPath());
            }
        }
    }

    private String getFilenameForKey(String key) {
        String suffix="jpg";
        if (key.lastIndexOf(".")!=-1){
            suffix=key.substring(key.lastIndexOf(".")+1);
        }
        int firstHalfLength = key.length() / 2;
        String localFilename = String.valueOf(key.substring(0, firstHalfLength).hashCode());
        localFilename += String.valueOf(key.substring(firstHalfLength).hashCode());
//        return localFilename;
        return localFilename+"."+suffix;
    }

    private void updateLog(String str){
        if (true)return;
        logBuilder.append(str).append("\n");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(logBuilder.toString());
            }
        });

    }

    /**
     * 初始化缓存
     * @param urls
     */
    private void initCacheMap(List<String> urls){
        for (String url: urls){
            String fileName=filesPath+File.separator+getFilenameForKey(url);
            if (checkFileExists(fileName)){
                Log.d("pan","url "+url+" cacheFile exist");
                cacheMap.put(url,fileName);
            }else {
                Log.d("pan","url "+url+" cacheFile not exist");
            }
        }
    }

    private boolean checkFileExists(String fileUrl){
        File file=new File(fileUrl);
        return file.exists();
    }

    private Handler mHandler=new Handler();
}
