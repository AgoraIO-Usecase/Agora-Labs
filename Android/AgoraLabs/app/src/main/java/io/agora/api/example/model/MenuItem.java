package io.agora.api.example.model;

public class MenuItem {
    private int id;
    private int imgRes;
    private int titleRes;

    public MenuItem(int id, int imgRes, int titleRes) {
        this.id = id;
        this.imgRes = imgRes;
        this.titleRes = titleRes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }
}
