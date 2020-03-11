/* BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2019 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 */
package shabale.android.com.storagesampleapp;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class SelectFilePath {
    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(uri,
                            new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String fileName = cursor.getString(0);
                        String path = Environment.getExternalStorageDirectory().toString()
                                + "/Download/" + fileName;
                        if (!TextUtils.isEmpty(path)) {
                            return path;
                        }
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }

                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    //In Android 8 and Android P the id is not a number
                    return uri.getPath().replaceFirst("^/document/raw:", "").
                            replaceFirst("^raw:", "");
                }
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    String filePath = uri.toString();
                    String fileId = filePath.substring(filePath.indexOf("primary") + 8);
                    String[] fileIds = fileId.split("/");
                    fileId = fileIds[fileIds.length - 1];
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1] + "/" + fileId;
                }
            }

            return uri.toString();
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        Log.v("SelectFilePath", "isGooglePhotosUri : uri.getAuthority() : " + uri.getAuthority());
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority()) || "com.google.android.apps.photos.contentprovider".equals(uri
                .getAuthority());
    }

    public static Cursor getMediaCursor(final Context context, final Uri originalUri) {
        if (originalUri != null) {
            Cursor cursor = context.getContentResolver().query(originalUri, null, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d("IPME","cursor : " + android.database.DatabaseUtils.dumpCursorToString(cursor));
                    return cursor;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Cursor getDocumentCursor(final Context context, final Uri originalUri) {
        if (originalUri != null) {
            final String docId = DocumentsContract.getDocumentId(originalUri);
            final String[] split = docId.split(":");
            final String type = split[0];
            if ("primary".equalsIgnoreCase(type)) {
                Uri mediauri = MediaStore.getMediaUri(context, originalUri);
                Log.d("IPME", "mediauri : " + mediauri);
                Cursor cursor = context.getContentResolver().query(mediauri, null, null, null, null, null);
                Log.d("IPME","cursor : " + android.database.DatabaseUtils.dumpCursorToString(cursor));
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        return cursor;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Uri getMediaUri(final Cursor cursor) {
        if (cursor != null) {
            final String[] split = cursor.getString(cursor.getColumnIndex
                    (MediaStore.MediaColumns.DOCUMENT_ID)).split(":");
            final String id = split[1];
            String mimetype = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            Log.d("IPME", "mimetype : " + mimetype);
            Uri contentUri = null;
            if (mimetype.startsWith("image/")) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (mimetype.startsWith("video/")) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if (mimetype.startsWith("audio/")) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if(mimetype.startsWith("application/")) {
                contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            Log.d("IPME", "contentUri : " + contentUri);
            Uri mediaUri = android.content.ContentUris.withAppendedId(
                    contentUri, Long.parseLong(id));
            Log.d("IPME", "mediaUri : " + mediaUri);
            return mediaUri;
        }
        return null;
    }

    public static Uri getDocumentUri(final Cursor cursor) {
        if (cursor != null) {
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            Log.d("IPME", "id : " + id);
            String mimetype = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            Log.d("IPME", "mimetype : " + mimetype);
            Uri contentUri = null;
            if (mimetype.startsWith("image/")) {
                contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if (mimetype.startsWith("video/")) {
                contentUri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if (mimetype.startsWith("audio/")) {
                contentUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if(mimetype.startsWith("application/")) {
                contentUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            Log.d("IPME", "contentUri : " + contentUri);
            Uri documentUri = ContentUris.withAppendedId(
                    contentUri, Long.valueOf(id));
            Log.d("IPME", "documentUri : " + documentUri);
            return documentUri;
        }
        return null;
    }

}

