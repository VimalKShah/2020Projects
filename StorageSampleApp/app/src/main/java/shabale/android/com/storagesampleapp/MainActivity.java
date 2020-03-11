package shabale.android.com.storagesampleapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText editText;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Storage Permissions
        verifyStoragePermissions(this);

        final String EXPIRY_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        String date = "2020-01-02T15:00:20Z";
        TimeZone timezone = TimeZone.getDefault();
        Log.d(TAG, "vimal "  + getMaxSizeStandAloneInBytes(this));


        /*String defaultTimezone = TimeZone.getDefault().getID();
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z")).parse(string.replaceAll("Z$", "+0000"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("string: " + string);
        System.out.println("defaultTimezone: " + defaultTimezone);
        System.out.println("date: " + (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date));
        long timeInMilliseconds = date.getTime();
        Log.d(TAG, "getMilliSecondsFromDate : timeInMilliseconds :: " + timeInMilliseconds);*/

        long timeInMilliseconds = 0l;
        SimpleDateFormat sdf = new SimpleDateFormat(EXPIRY_TIMESTAMP_FORMAT);
        try {
            sdf.setTimeZone(timezone);
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
            Log.d(TAG, "getMilliSecondsFromDate : timeInMilliseconds :: " + timeInMilliseconds);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        Button clickVideo = findViewById(R.id.clickVideo);
        editText = findViewById(R.id.editText);
        clickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                //innerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                //innerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //innerIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                innerIntent.setType("*/*");
                startActivityForResult(innerIntent, 100);

                /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/amr");
                intent.setClassName("com.android.soundrecorder",
                        "com.android.soundrecorder.SoundRecorder");
                intent.putExtra(android.provider.MediaStore.Audio.Media.EXTRA_MAX_BYTES, 100);
                intent.putExtra("exit_after_record", true);
                startActivityForResult(intent, 100);*/
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("MainActivity","onActivityResult requestCode : "+requestCode);
        Log.d("MainActivity","onActivityResult resultCode : "+resultCode);
        /*if (resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = Uri.parse("content://media/external/downloads/313");//data.getData();
            Log.i("MainActivity", "uri: " + uri);
            if (SelectFilePath.isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                Log.d("MainActivity", split.length + " ");
                String type = split[0];
                Log.d("MainActivity", split[0] + " : " + split[1]);
                File file = new File(split[1]);
                Uri mediauri = Uri.fromFile(file);


                long size = file.length();
                Log.d("MainActivity", "size : " + size);
                String fileName = file.getName();
                Log.d("MainActivity", "fileName : " + fileName);
                Uri contentUri1 = Uri.fromFile(file);
                Log.d("MainActivity", "contentUri : " + contentUri1);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(contentUri1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Uri mediauri = MediaStore.getMediaUri(this, uri);
                //Uri mediauri = MediaStore.getDocumentUri(this, uri);
                Log.d("MainActivity", " " + file.canRead());

                Log.d("MainActivity", " " + mediauri);

                if ("primary".equalsIgnoreCase(type)) {


                    Cursor cursor = getContentResolver().query(uri, null
                            , null, null, null, null);
                    Log.d("MainActivity", "split1 : " + DatabaseUtils.dumpCursorToString(cursor));
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            int id = cursor.getColumnIndex("_id");
                            Log.d("MainActivity",
                                    "id : " + cursor.getString(id) + " : " + id);
                            final String[] split1 =
                                    cursor.getString(id).split(":");
                            Log.d("MainActivity", "split1 : " + split1);
                            Log.i("MainActivity", "id: " + id);
                            String mimetype =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                            Log.i("MainActivity", "mimetype: " + mimetype +
                                    " : " + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype));
                            Uri contentUri = null;
                            if (mimetype.startsWith("image/")) {
                                contentUri =
                                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                            } else if (mimetype.startsWith("video/")) {
                                contentUri =
                                        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                            } else if (mimetype.startsWith("audio/")) {
                                contentUri =
                                        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                            }
                            Uri documentUri = ContentUris.withAppendedId(contentUri,
                                    Long.valueOf(cursor.getString(id)));
                            Log.i("MainActivity", "contentUri: " + contentUri);
                            Log.i("MainActivity", "documentUri: " + documentUri);
                            saveTempFile("vimal", this, contentUri);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if(SelectFilePath.isDownloadsDocument(uri)) {
                Cursor cursor = getContentResolver().query(uri,
                        null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d("MainActivity", DatabaseUtils.dumpCursorToString(cursor));
                    int id = cursor.getColumnIndex("_id");
                    *//*Log.d("MainActivity",
                            "id : " + cursor.getString(id) + " : " + id);
                    final String[] split1 =
                            cursor.getString(id).split(":");
                    Log.d("MainActivity", "split1 : " + split1);
                    Log.i("MainActivity", "id: " + id);*//*

                    id = cursor.getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID);
                    final String[] split = cursor.getString(id).split(":");
                    final String type = split[1];
                    Uri documentUri = null;
                    if(split[0].contains("raw")) {
                        documentUri = Uri.parse(type.replaceFirst("raw:", ""));
                        cursor = getContentResolver().query(documentUri,
                                null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            Log.d("MainActivity", DatabaseUtils.dumpCursorToString(cursor));
                        }
                    } else {
                        Log.i("MainActivity", "id: " + id);
                        Log.i("MainActivity", "id: " + type);
                        String mimetype =
                                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                        Log.i("MainActivity", "mimetype: " + mimetype +
                                " : " + MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype));
                        Uri contentUri = null;
                        if (mimetype.startsWith("image/")) {
                            contentUri =
                                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                        } else if (mimetype.startsWith("video/")) {
                            contentUri =
                                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                        } else if (mimetype.startsWith("audio/")) {
                            contentUri =
                                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                        }
                        MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                        documentUri = ContentUris.withAppendedId(contentUri,
                                Long.valueOf(type));
                        Log.i("MainActivity", "contentUri: " + contentUri);
                        Log.i("MainActivity", "documentUri: " + documentUri);
                    }
                    saveTempFile("vimal", this, documentUri);

                }
            } else if (SelectFilePath.isMediaDocument(uri)) {
                Cursor cursor = getContentResolver().query(uri, null,
                        null, null, null, null);

                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        Log.d("MainActivity", DatabaseUtils.dumpCursorToString(cursor));
                        String displayName = cursor.getString(
                                cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.i("MainActivity", "Display Name: " + displayName);

                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        int id = cursor.getColumnIndex(MediaStore.MediaColumns.DOCUMENT_ID);
                        final String[] split = cursor.getString(id).split(":");
                        final String type = split[1];
                        Log.i("MainActivity", "id: " + id);
                        Log.i("MainActivity", "id: " + type);


                        Uri contentUri = ContentUris.withAppendedId(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(type));
                        Log.i("MainActivity", "id: " + contentUri);
                        String size = null;
                        if (!cursor.isNull(sizeIndex)) {
                            size = cursor.getString(sizeIndex);
                        } else {
                            size = "Unknown";
                        }

                        saveTempFile(displayName, this, contentUri);


                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            } else {
                SelectFilePath.getPath(this, uri);
                saveTempFile("vimal", this, uri);
            }
        }*/

        writeDataInExternalStorage(Uri.parse("content://media/external/downloads/312"));

    }

    public void writeDataInExternalStorage(Uri uri) {
        this.grantUriPermission(this.getPackageName(), uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Log.d(TAG, "uri : " + uri);

        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();

        /*String mimeType = resolver.getType(uri);
        String type = MimeTypeMap.getSingleton().
                getExtensionFromMimeType(mimeType);

        String displayName =  "Vimal" + "_" + System.currentTimeMillis() + "." + type;

        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);*/

        //values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/playground");
        //values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/playground");

        //Uri localFileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        Uri localFileUri = uri;

        Log.d(TAG, "localFileUri : " + localFileUri);
        String id = localFileUri.getLastPathSegment();
        OutputStream out = null;
        InputStream in = null;
        long size = 0;
        try {
            in = resolver.openInputStream(uri);
            out = resolver.openOutputStream(localFileUri);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = in.read(buffer)) != -1) {
                Log.d("MainActivity","read : "+read);
                out.write(buffer, 0, read);
                size += read;
            }
            Log.d("MainActivity","exit : ");
            out.flush();
            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        values.put(MediaStore.Images.Media.SIZE, size);
        resolver.update(localFileUri, values, null, null);

        String fileName = null;
        Cursor cursor;
        String selectFilePath = null;
        //localFileUri = Uri.parse("content://media/external/downloads/116");
        Log.d(TAG, "localFileUri : " + localFileUri);

        if (SelectFilePath.isMediaDocument(localFileUri)) {
            cursor = SelectFilePath.getMediaCursor(this, localFileUri);

            if (cursor != null) {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                Log.d(TAG, "size : " + size);
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.d(TAG, "fileName : " + fileName);
                selectFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.d(TAG, "selectFilePath : " + selectFilePath);
            }
        } else if (SelectFilePath.isExternalStorageDocument(localFileUri)) {
            cursor = SelectFilePath.getDocumentCursor(this, localFileUri);

            if (cursor != null) {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                Log.d(TAG, "size : " + size);
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.d(TAG, "fileName : " + fileName);
                selectFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.d(TAG, "selectFilePath : " + selectFilePath);
            }
        } else if (SelectFilePath.isDownloadsDocument(localFileUri)) {
            cursor = SelectFilePath.getMediaCursor(this, localFileUri);

            if (cursor != null) {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                Log.d(TAG, "size : " + size);
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.d(TAG, "fileName : " + fileName);
                selectFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.d(TAG, "selectFilePath : " + selectFilePath);
            }
        } else if ("content".equalsIgnoreCase(localFileUri.getScheme())) {
            cursor = SelectFilePath.getMediaCursor(this, localFileUri);

            if (cursor != null) {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(OpenableColumns.SIZE)));
                Log.d(TAG, "size : " + size);
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.d(TAG, "fileName : " + fileName);
                selectFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                Log.d(TAG, "selectFilePath : " + selectFilePath);
            }
        } else {
            selectFilePath = SelectFilePath.getPath(this, localFileUri);
            Log.v(TAG, "selectFilePath : " + selectFilePath);
            File file = new File(selectFilePath);
            size = file.length();
            Log.d(TAG, "size : " + size);
            fileName = file.getName();
            Log.d(TAG, "fileName : " + fileName);
        }



    }



    public File saveTempFile(String fileName, Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        File file = null;

        //InputStream in = null;
        FileOutputStream out = null;
        InputStream in = null;

        try {
            Log.d("MainActivity","uri : "+uri);
            //Log.d("MainActivity","getreal : "+getRealPathFromURI_API19(context, uri));
            Log.d("MainActivity","fileName : "+fileName);
            //Log.d("MainActivity","getMediaUri : "+MediaStore.getMediaUri(context, uri));
            String selectFilePath = SelectFilePath.getPath(getApplicationContext(),
                    uri);
            Log.d("MainActivity","selectFilePath : "+selectFilePath);
            //file = new File(selectFilePath);
            //in = new FileInputStream(file);
            in = resolver.openInputStream(uri);

            //in = resolver.openInputStream(fileInputStream);
            //in = file1.
            //Bitmap artwork = BitmapFactory.decodeStream(in);
            File folder = context.getExternalFilesDir("StorageSampleApp");
            if (!folder.exists()) {
                boolean mkdir = folder.mkdirs();
                Log.d("MainActivity",  "dir : " + mkdir);
            }
            file = new File(folder,fileName);
            Log.d("MainActivity","path  : "+file.getPath());
            //boolean createfile = file.createNewFile();
            //Log.d("MainActivity",  "createfile : " + createfile);
//            mFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR, fileName);
            out = new FileOutputStream(file, false);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                Log.d("MainActivity","read : "+read);
                out.write(buffer, 0, read);
            }
            Log.d("MainActivity","exit : ");
            out.flush();
        } catch (IOException e) {
            Log.e("MainActivity", "", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e("MainActivity", "", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e("MainActivity", "", e);
                }
            }
        }
        return file;
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = getApplicationContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static long getMaxSizeStandAloneInBytes(Context context) {
        int maxSizeStandAlone = 1;
        return (maxSizeStandAlone > 0)?maxSizeStandAlone:(1024 * 1024);
    }



}
