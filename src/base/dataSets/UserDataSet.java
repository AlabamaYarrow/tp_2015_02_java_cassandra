package base.dataSets;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
public class UserDataSet implements Serializable { // Serializable Important to Hibernate!
    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column()
    private String password;

    @Column
    private String email;

    @Column
    private int scoreTotal;

    //Important to Hibernate!
    public UserDataSet() {
    }

    public UserDataSet(long id, String name) {
        this.setId(id);
        this.setName(name);
    }

    public UserDataSet(String email, String name, String password) {
        this.setId(-1);
        this.setName(name);
        this.email = email;
        this.password = password;
        this.scoreTotal = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserDataSet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public String getEmail() {
        return this.email;
    }

    public long getID() {
        return this.id;
    }

    public int getScoreTotal() {
        return this.scoreTotal;
    }

    public Map<Object, Object> getHydrated() {
        Map<Object, Object> map = new HashMap<>();
        this.hydrate(map);
        return map;
    }

    public void hydrate(Map<Object, Object> map) {
        map.put("id", this.id);
        map.put("email", this.email);
        map.put("name", this.name);
        map.put("scoreTotal", this.scoreTotal);
    }
}
