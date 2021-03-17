package entities;

import javax.persistence.*;

@Entity
@Table(name = "Clients")
public class Client {
    private long client_id;
    private String client_name;
    private String phone;
    private boolean is_client_removed;

    public Client() {}

    public Client(long client_id, String client_name, String phone, boolean is_client_removed) {
        this.client_id = client_id;
        this.client_name = client_name;
        this.phone = phone;
        this.is_client_removed = is_client_removed;
    }

    @Column(name = "client_name")
    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "is_client_removed")
    public boolean getIs_client_removed() {
        return is_client_removed;
    }

    public void setIs_client_removed(boolean is_client_removed) {
        this.is_client_removed = is_client_removed;
    }

    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getClient_id() {
        return client_id;
    }

    public void setClient_id(long client_id) {
        this.client_id = client_id;
    }
}