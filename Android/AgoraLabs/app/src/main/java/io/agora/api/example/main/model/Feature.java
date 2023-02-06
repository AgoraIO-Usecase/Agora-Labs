package io.agora.api.example.main.model;

public class Feature {


    private int id;
    private int iconRes;
    private int titleRes;
    private boolean enabled=false;

    public Feature(int id, int iconRes, int titleRes) {
        this.id = id;
        this.iconRes = iconRes;
        this.titleRes = titleRes;
    }

    public Feature(int id, int iconRes, int titleRes,boolean enabled) {
        this.id = id;
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.enabled=enabled;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }


    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
