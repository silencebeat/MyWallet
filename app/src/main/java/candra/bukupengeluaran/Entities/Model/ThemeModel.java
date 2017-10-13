package candra.bukupengeluaran.Entities.Model;

/**
 * Created by Candra Triyadi on 08/10/2017.
 */

public class ThemeModel {

    String name;
    int image;
    boolean isActive;

    public ThemeModel(String name, int image, boolean isActive) {
        this.name = name;
        this.image = image;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
