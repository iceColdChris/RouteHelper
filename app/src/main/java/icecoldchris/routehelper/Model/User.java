package icecoldchris.routehelper.Model;

import io.realm.RealmObject;
import io.realm.annotations.Required;


public class User extends RealmObject {

    @Required
    private String username;

    @Required
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
