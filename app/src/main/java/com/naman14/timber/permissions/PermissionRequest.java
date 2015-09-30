package com.naman14.timber.permissions;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Michal Tajchert on 2015-06-04.
 */
public class PermissionRequest {
    private static Random random;
    private ArrayList<String> permissions;
    private int requestCode;
    private PermissionCallback permissionCallback;

    public PermissionRequest(int requestCode) {
        this.requestCode = requestCode;
    }

    public PermissionRequest(ArrayList<String> permissions, PermissionCallback permissionCallback) {
        this.permissions = permissions;
        this.permissionCallback = permissionCallback;
        if(random == null) {
            random = new Random();
        }
        this.requestCode = random.nextInt(32768);
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public PermissionCallback getPermissionCallback() {
        return permissionCallback;
    }

    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }
        if(object instanceof PermissionRequest) {
            return ((PermissionRequest) object).requestCode == this.requestCode;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return requestCode;
    }
}