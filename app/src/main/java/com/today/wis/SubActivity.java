package com.today.wis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SubActivity extends AppCompatActivity
{
    private TextView tvSubWise, tvSubName;
    private ImageButton imgSubShare, imgSubCamera, imgSubBack;
    private LinearLayout laySubScreen;
    private static final String CAPTURE_PATH = "/wise_save";

    private String strWise, strName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        tvSubWise = findViewById(R.id.tv_wise_sub);
        tvSubName = findViewById(R.id.tv_hum_sub);
        imgSubShare = findViewById(R.id.imgbtn_sub_share);
        imgSubCamera = findViewById(R.id.imgbtn_sub_camera);
        imgSubBack = findViewById(R.id.imgbtn_sub_back);
        laySubScreen = findViewById(R.id.lay_screen_sub);

        Intent i = getIntent();
        strWise = i.getStringExtra("wiseSub");
        strName = i.getStringExtra("nameSub");
        tvSubWise.setText(strWise);
        tvSubName.setText(strName);

        imgSubShare.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                shareWise();
            }
        });

        imgSubCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                captureView(laySubScreen);
            }
        });

        imgSubBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    public void shareWise()
    {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        String text = strWise + "\n-" + strName + "\n\n다운받고 오늘의 명언 무료로 보기\n앱스토어:https://play.google.com/store/apps/details?id=com.nhn.android.search";
        intent.putExtra(Intent.EXTRA_TEXT, text);

        Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
        startActivity(chooser);

    }

    public void captureView(View View)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(SubActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
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
                MediaScanner scanner = MediaScanner.newInstance(SubActivity.this);
                scanner.mediaScanning(strFilePath);
                Crouton.makeText(SubActivity.this, R.string.save_wise, Style.INFO).show();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Crouton.makeText(SubActivity.this, R.string.fail_save, Style.ALERT).show();
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
                Crouton.makeText(SubActivity.this, R.string.per_yes, Style.INFO).show();
            }
            else
            {
                Crouton.makeText(SubActivity.this, R.string.per_no, Style.ALERT).show();
            }
        }
    }
}