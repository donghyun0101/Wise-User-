package com.today.wis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends AppCompatActivity
{
    private LinearLayout layScreen;
    private ImageButton imgList, imgCamera, imgShare;
    private TextView tvWise, tvName;
    private static final String CAPTURE_PATH = "/wise_save";

    public String[] wise, name;
    private int setNumber;

    Intent serviceIntent; //2020-02-25 추가

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new WiseLoadingTask().execute();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        /*
        //2020-02-25 주석처리
        Intent intent = new Intent(getApplicationContext(), StartService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startService(intent);
         */

        //2020-02-25 추가
        if (StartService.serviceIntent == null)
        {
            serviceIntent = new Intent(getApplicationContext(), StartService.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            Intent startserviceintent = new Intent(MainActivity.this, StartService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                startForegroundService(startserviceintent);
            }
            else
            {
                startService(startserviceintent);
            }
        }
        else
        {
            serviceIntent = StartService.serviceIntent;
        }

        layScreen = findViewById(R.id.lay_screen);
        imgList = findViewById(R.id.imgbtn_list);
        imgCamera = findViewById(R.id.imgbtn_camera);
        imgShare = findViewById(R.id.imgbtn_share);
        tvWise = findViewById(R.id.tv_wise);
        tvName = findViewById(R.id.tv_hum);

        imgList.setClickable(false);
        imgCamera.setClickable(false);
        imgShare.setClickable(false);

        imgList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this, WiseListActivity.class);
                i.putExtra("wise", wise);
                i.putExtra("name", name);
                startActivity(i);
            }
        });

        imgShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareWise();
            }
        });

        imgCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                captureView(layScreen);
            }
        });
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.e("state", "onRestart");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("state", "onResume");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e("state", "onDestroy");

        //2020-02-25 추가
        if (serviceIntent != null)
        {
            stopService(serviceIntent);
            serviceIntent = null;
        }

        Crouton.cancelAllCroutons();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.e("state", "onPause");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.e("state", "onStart");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.e("state", "onStop");
    }

    public void shareWise()
    {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        String text = wise[setNumber] + "\n-" + name[setNumber] + "\n\n다운받고 오늘의 명언 무료로 보기\n앱스토어:https://play.google.com/store/apps/details?id=com.nhn.android.search";
        intent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        startActivity(chooser);

    }

    public void captureView(View View)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        else
        {
            View.buildDrawingCache();
            Bitmap captureView = View.getDrawingCache();
            FileOutputStream fos;

            String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures" + CAPTURE_PATH;
            File folder = new File(strFolderPath);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
            File fileCacheItem = new File(strFilePath);

            try
            {
                fos = new FileOutputStream(fileCacheItem);
                captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
                MediaScanner scanner = MediaScanner.newInstance(MainActivity.this);
                scanner.mediaScanning(strFilePath);
                Crouton.makeText(MainActivity.this, R.string.save_wise, Style.INFO).show();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Crouton.makeText(MainActivity.this, R.string.fail_save, Style.ALERT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode == 0)
        {
            if (grantResults[0] == 0)
            {
                Crouton.makeText(MainActivity.this, R.string.per_yes, Style.INFO).show();
            }
            else
            {
                Crouton.makeText(MainActivity.this, R.string.per_no, Style.ALERT).show();
            }
        }
    }

    public void getJsonWise()
    {
        try
        {
            String line = getStringFromUrl("http://kdonghyun0101.dothome.co.kr/data_insert.php");

            // 원격에서 읽어온 데이터로 JSON 객체 생성
            JSONObject object = new JSONObject(line);

            // "kkt_list" 배열로 구성 되어있으므로 JSON 배열생성
            JSONArray Array = new JSONArray(object.getString("wiselist"));

            wise = new String[Array.length()];
            name = new String[Array.length()];

            for (int i = 0; i < Array.length(); i++)
            {
                JSONObject insideObject = Array.getJSONObject(i);

                wise[i] = insideObject.getString("wise");
                name[i] = insideObject.getString("name");
                setNumber = i;

            } // for
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // getStringFromUrl : 주어진 URL 페이지를 문자열로 얻는다.
    public String getStringFromUrl(String url) throws UnsupportedEncodingException
    {

        // 입력스트림을 "UTF-8" 를 사용해서 읽은 후, 라인 단위로 데이터를 읽을 수 있는 BufferedReader 를 생성한다.
        BufferedReader br = new BufferedReader(new InputStreamReader(getInputStreamFromUrl(url), "UTF-8"));

        // 읽은 데이터를 저장한 StringBuffer 를 생성한다.
        StringBuffer sb = new StringBuffer();

        try
        {
            // 라인 단위로 읽은 데이터를 임시 저장한 문자열 변수 line
            String line = null;

            // 라인 단위로 데이터를 읽어서 StringBuffer 에 저장한다.
            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    } // getStringFromUrl


    // getInputStreamFromUrl : 주어진 URL 에 대한 입력 스트림(InputStream)을 얻는다.
    public static InputStream getInputStreamFromUrl(String url)
    {
        InputStream contentStream = null;
        try
        {
            // HttpClient 를 사용해서 주어진 URL에 대한 입력 스트림을 얻는다.
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(url));
            contentStream = response.getEntity().getContent();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return contentStream;
    } // getInputStreamFromUrl


    // AsyncTask 를 이용 UI 처리 및 Background 작업 등을 하나의 클래스에서 작업 할 수 있도록 지원해준다.
    private class WiseLoadingTask extends AsyncTask
    {
        @Override
        protected Object doInBackground(Object[] objects)
        {
            getJsonWise();
            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            tvWise.setText(wise[setNumber]);
            tvName.setText(name[setNumber]);

            imgList.setClickable(true);
            imgCamera.setClickable(true);
            imgShare.setClickable(true);
            super.onPostExecute(o);
        }
    }
}