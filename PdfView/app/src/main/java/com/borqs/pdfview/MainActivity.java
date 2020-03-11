package com.borqs.pdfview;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mDownload, mView;

    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void request(View view) {

        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 112);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 112);

        mDownload = findViewById(R.id.btn_download);
        mView = findViewById(R.id.btn_view);
        mDownload.setOnClickListener(this);
        mView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_download:
                //download file and store it in external folder;
                download(view);
                break;
            case R.id.btn_view:
                //view th downloaded file
                viewFile();
                break;
        }
    }

    private void download(View v) {
        Log.v("shwetha", "download() Method invoked ");
        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            Log.v("shwetha", "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();
        } else {
            Log.v("shwetha", "download() Method HAVE PERMISSIONS ");
            //new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
            //new DownloadFile().execute("https://gahp.net/wp-content/uploads/2017/09/sample.pdf", "mav.pdf");
            String fileUrl = "http://reference1.mapinfo.com/software/anysite/english_AU/tutorials/Sample-Sales-Data.xlsx";
            String extensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
            Log.d("shwetha","extensionFromUrl: "+extensionFromUrl);
            new DownloadFile().execute("http://reference1.mapinfo.com/software/anysite/english_AU/tutorials/Sample-Sales-Data.xlsx", "my.xlsx");
        }
        Log.v("shwetha", "download() Method completed ");
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            /*String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File pdfFile = new File(folder, fileName);*/


            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "testthreepdf");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    private void viewFile() {

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {

            Log.v("shwetha", "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
            t.show();

        } else {
            //File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);  // -> filename = maven.pdf
            File d = Environment.getExternalStorageDirectory();  // -> filename = maven.pdf
            File folder = new File(d, "testthreepdf");

            File pdfFile = new File(folder, "mavenn.pdf");

            Log.v("shwetha", "view() Method pdfFile " + pdfFile.getAbsolutePath());

            Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);


            Log.v("shwetha", "view() Method path " + path);

            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
