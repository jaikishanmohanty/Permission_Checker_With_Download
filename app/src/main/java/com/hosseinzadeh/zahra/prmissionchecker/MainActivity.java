package com.hosseinzadeh.zahra.prmissionchecker;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity implements OnAppClickListener {

    private ListView appListView;
    RecyclerView mRecyclerView;
    AppListAdapter adapter;
    ArrayList<AppItem> apps;
    private Context context;
    private boolean showSystemApps = false;
    private AppListAdapter appListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mRecyclerView = findViewById(R.id.appList);
        apps = getInstalledPackages();

        adapter = new AppListAdapter(apps, getApplicationContext());
        mRecyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        adapter.setClickListener(this);
        context = this;

        ToggleButton toggleButton = findViewById(R.id.system_apps_toggle_button);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
         //   showSystemApps = isChecked;
            //      updateAppListAllApps();
            updateAppListInstalledApps();

        });


    }

    protected ArrayList<AppItem> getInstalledPackages() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        ArrayList<AppItem> applist = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String packageName = activityInfo.applicationInfo.packageName;
            String label = (String) packageManager.getApplicationLabel(activityInfo.applicationInfo);
            Drawable icon = packageManager.getApplicationIcon(activityInfo.applicationInfo);
            AppItem item = new AppItem();
            item.setPackageName(packageName);
            item.setAppName(label);
            item.setAppIcon(icon);
            applist.add(item);
        }
        return applist;
    }

    protected String getPermissionsByPackageName(String packageName) {
        StringBuilder builder = new StringBuilder();

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            int counter = 1;
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission = packageInfo.requestedPermissions[i];
                    builder.append("" + counter + ". " + permission + "\n");
                    counter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Override
    public void onAppSelected(View view, int position) {
        AppItem app = apps.get(position);
        String permissions = getPermissionsByPackageName(app.getPackageName());
        Intent i = new Intent(MainActivity.this, ViewPermissions.class);
        Bitmap bmp = DrawableToBitmap.drawableToBitmap(app.getAppIcon());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        i.putExtra("icon", byteArray);
        i.putExtra("name", app.getAppName());
        i.putExtra("package", app.getPackageName());
        i.putExtra("permissions", permissions);
        startActivity(i);
//        Toast.makeText(MainActivity.this, permissions, Toast.LENGTH_LONG).show();
    }

    private void updateAppListInstalledApps() {
        List<AppInfo> appList = new ArrayList<>();

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // Skip system apps
                continue;
            }
            String name = packageInfo.applicationInfo.loadLabel(pm).toString();
            String packageName = packageInfo.packageName;
            String category = getCategory(pm, packageName);
            System.out.println("Appname: " + name + " Category : " + category);
            List<String> permissions = new ArrayList<>();
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    if (pm.checkPermission(permission, packageName) == PackageManager.PERMISSION_GRANTED) {
                        permissions.add(permission);
                    }
                }
            }

            appList.add(new AppInfo(name, packageName, category, permissions));
        }

//        appListAdapter.clear();
//        appListAdapter.addAll(appList);
 //       appListAdapter.notifyDataSetChanged();

        // Export permissions and category to Excel file
        try {
            ExcelExporter exporter = new ExcelExporter();
            exporter.exportToExcel(context, appList, "installedAppsCategory.xls");
            Toast.makeText(context, "Permissions and category exported successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting app permissions and category", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCategory(PackageManager pm, String packageName) {
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (info.category != AppInfo.CATEGORY_UNDEFINED) {
//                    return String.valueOf(info.category);
//                }
//            }

            // Use Google Play Store API to fetch category
            String url = "https://play.google.com/store/apps/details?id=" + packageName;
            System.out.println("URL : "+url);
            String category = new GetCategoryTask().execute(url).get();

            return category;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    private class GetCategoryTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                Document doc = Jsoup.connect(urls[0]).get();
                String category = doc.select("[itemprop=genre]").first().text();
                System.out.println("Itemprop Category : "+category);
                return category;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
