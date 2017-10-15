package candra.bukupengeluaran.Entities.Model.Firestore;

/**
 * Created by Candra Triyadi on 15/10/2017.
 */

public class Users {

    String email;
    String data;

    public Users(){}

    public Users(String email, String data) {
        this.email = email;
        this.data = data;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public String getData() {
        return data;
    }
}
