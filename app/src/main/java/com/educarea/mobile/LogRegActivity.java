package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LogRegActivity extends AppCompatActivity {

    private TextView textVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_reg);
        textVersion = findViewById(R.id.textViewVersion);
        textVersion.setText(getResources().getString(R.string.app_version)+" "+BuildConfig.VERSION_NAME);
    }

    public void onClickSignUp(View view){
        Intent intent = new Intent(LogRegActivity.this, InputLogPassActivity.class);
        intent.putExtra(InputLogPassActivity.ENTER_MODE, InputLogPassActivity.MODE_SIGN_UP);
        startActivity(intent);
    }

    public void onClickLogIn(View view){
        Intent intent = new Intent(LogRegActivity.this, InputLogPassActivity.class);
        intent.putExtra(InputLogPassActivity.ENTER_MODE, InputLogPassActivity.MODE_LOG_IN);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
