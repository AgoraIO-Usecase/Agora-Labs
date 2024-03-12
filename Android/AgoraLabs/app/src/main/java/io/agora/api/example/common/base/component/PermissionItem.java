package io.agora.api.example.common.base.component;

public class PermissionItem {
    public String permissionName;
    public boolean granted = false;
    public int requestId;

    PermissionItem(String name, int reqId) {
        permissionName = name;
        requestId = reqId;
    }
}
