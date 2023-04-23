package com.hosseinzadeh.zahra.prmissionchecker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPermissions extends AppCompatActivity {

    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_permissions);
        TextView tvAppName = findViewById(R.id.tvAppNameView);
        ImageView icon = findViewById(R.id.appIconView);
        TextView tvPermissions = findViewById(R.id.tvPermissions);
        Button btnChange = findViewById(R.id.btnChange);

        Bundle extras = getIntent().getExtras();
        byte[] b = extras.getByteArray("icon");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        icon.setImageBitmap(bmp);

        tvAppName.setText(extras.getString("name"));

        tvPermissions.setText(extras.getString("permissions"));

        packageName = extras.getString("package");

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", packageName, null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

    }
}
