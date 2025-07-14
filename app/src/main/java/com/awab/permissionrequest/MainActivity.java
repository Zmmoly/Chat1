
package com.awab.permissionrequest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private ScrollView scrollView;
    private EditText messageInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatLayout = findViewById(R.id.chatLayout);
        scrollView = findViewById(R.id.scrollView);
        messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    processMessage(message);
                    messageInput.setText("");
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                }
            }
        });
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
}
