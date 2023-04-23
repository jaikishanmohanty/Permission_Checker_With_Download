package com.hosseinzadeh.zahra.prmissionchecker;

import java.util.List;


public class AppInfo {
    private String name;
    private String packageName;
    private String category;
    private List<String> permissions;
    public static final int CATEGORY_UNDEFINED = -1;


    public AppInfo(String name, String packageName, String category, List<String> permissions) {
        this.name = name;
        this.packageName = packageName;
        this.category = category;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}



