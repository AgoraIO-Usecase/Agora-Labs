package io.agora.api.example.model;

public class OptionItem {

    private int id;
    private int imgRes;
    private int titleRes;
    private boolean selected=false;

    public OptionItem(int id, int imgRes, int titleRes) {
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
