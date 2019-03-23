package com.example.myapplication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private ProgressDialog dialog;
    private String image_path = "https://imgsrc.baidu.com/forum/pic/item/7c1ed21b0ef41bd51a5ac36451da81cb39db3d10.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);
        dialog.setTitle("提示");
        dialog.setMessage("正在下载图像，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        imageView = (ImageView)findViewById(R.id.imageview1);
        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownTask().execute(image_path);
            }
        });
    }

    private class DownTask extends AsyncTask<String, Integer, byte[]>{
        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected void onPostExecute(byte[] result) {
            super.onPostExecute(result);
            Bitmap bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
            imageView.setImageBitmap(bitmap);
            dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgressStyle(values[0]);
        }

        @Override
        protected byte[] doInBackground(String... strings) {
            Bitmap bitmap = null;
            byte[] result = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setReadTimeout(3000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    InputStream is = urlConnection.getInputStream();
                    long file_length = urlConnection.getContentLength();
                    int total_length = 0;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    while (-1 != (len = is.read(data))){
                        total_length += len;
                        int progress_value = (int)((total_length / (float)file_length)*100);
                        publishProgress(progress_value);
                        baos.write(data, 0, len);
                        baos.flush();
                    }
//                    bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, data.length);
                    result = baos.toByteArray();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }
    }

}
