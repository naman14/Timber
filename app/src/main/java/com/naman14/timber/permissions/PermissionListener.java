package com.naman14.timber.permissions;

public interface PermissionListener {
    /**
     * Gets called each time we run Nammu.permissionCompare() and some Permission is revoke/granted to us
     * @param permissionChanged
     */
    public void permissionsChanged(String permissionChanged);

    /**
     * Gets called each time we run Nammu.permissionCompare() and some Permission is granted
     * @param permissionGranted
     */
    public void permissionsGranted(String permissionGranted);

    /**
     * Gets called each time we run Nammu.permissionCompare() and some Permission is removed
     * @param permissionRemoved
     */
    public void permissionsRemoved(String permissionRemoved);
}