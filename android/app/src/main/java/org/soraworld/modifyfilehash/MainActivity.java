package org.soraworld.modifyfilehash;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss", Locale.CHINA);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickModify(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String message = "";
            try {
                File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (downloadDir != null) {
                    File modifyDir = new File(downloadDir, "ModifiedHashFiles");
                    if (!modifyDir.exists()) {
                        modifyDir.mkdirs();
                    }
                    if (modifyDir.isDirectory()) {
                        Uri uri = data.getData();
                        String filename = getRealNameFromURI(getApplicationContext(), uri);
                        int index = filename.lastIndexOf('.');
                        if (index > 0) {
                            filename = new StringBuilder(filename).insert(index, "_modify").toString();
                        } else {
                            filename = filename + "_modify";
                        }
                        Path newFile = modifyDir.toPath().resolve(filename);
                        SwitchMaterial btn = findViewById(R.id.btnSwitch);
                        if (btn.isChecked() || (Files.exists(newFile) && Files.isDirectory(newFile))) {
                            Files.delete(newFile);
                        }
                        if (!Files.exists(newFile)) {
                            InputStream in = getContentResolver().openInputStream(uri);
                            Files.copy(in, newFile);
                            String mark = DATE_FORMAT.format(new Date()) + " modified by Himmelt.";
                            Files.write(newFile, mark.getBytes(), StandardOpenOption.APPEND);
                            message = "文件 Hash 修改完成, 追加: " + mark + " \n";
                        } else {
                            message = "文件 " + newFile + " 已存在.\n";
                        }
                    } else {
                        message = "文件 " + modifyDir + " 不是文件夹.\n";
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                message = e.toString();
            }
            TextView textView = findViewById(R.id.textView);
            textView.append(message);
        }
    }

    public static String getRealNameFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DISPLAY_NAME};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
