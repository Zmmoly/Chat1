
package com.awab.permissionrequest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private ScrollView scrollView;
    private EditText messageInput;
    private static final int PERMISSION_REQUEST_CODE = 123;

    private static final String[] normalPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatLayout = findViewById(R.id.chatLayout);
        scrollView = findViewById(R.id.scrollView);
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                processMessage(message);
                messageInput.setText("");
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }
        });

        requestPermissionsProperly();
    }

    private void requestPermissionsProperly() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> toRequest = new ArrayList<>();
            for (String perm : normalPermissions) {
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    toRequest.add(perm);
                }
            }
            if (!toRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
            }
            requestSpecialPermissions();
        }
    }

    private void requestSpecialPermissions() {
        // MANAGE_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                addMessage("مساعد: الرجاء منح صلاحية الوصول الكامل للملفات من الإعدادات.");
            }
        }

        // ACCESS_BACKGROUND_LOCATION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                addMessage("مساعد: الرجاء منح صلاحية الموقع في الخلفية من إعدادات التطبيق.");
            }
        }
    }

    private void processMessage(String message) {
        addMessage("أنت: " + message);

        if (message.toLowerCase().startsWith("افتح ")) {
            String appName = message.substring(5).trim().toLowerCase();
            switch (appName) {
                case "واتساب":
                case "whatsapp":
                    openApp("com.whatsapp");
                    break;
                case "يوتيوب":
                case "youtube":
                    openApp("com.google.android.youtube");
                    break;
                case "كروم":
                case "chrome":
                    openApp("com.android.chrome");
                    break;
                default:
                    addMessage("مساعد: لم أتعرف على التطبيق '" + appName + "'");
            }
        }
    }

    private void openApp(String packageName) {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
            addMessage("مساعد: جاري فتح التطبيق...");
        } else {
            addMessage("مساعد: التطبيق غير مثبت على الجهاز.");
        }
    }

    private void addMessage(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(8, 8, 8, 8);
        chatLayout.addView(textView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String perm = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    addMessage("✅ تم منح الإذن: " + perm);
                } else {
                    addMessage("❌ تم رفض الإذن: " + perm);
                }
            }
        }
    }
}
