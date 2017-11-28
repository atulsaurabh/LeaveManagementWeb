package org.leavemanagement.entity;

import javax.persistence.*;

/**
 * Created by atul_saurabh on 19/11/17.
 */

@Entity
public class User
{
    private int userid;
    private String username;
    private String password;
    private String rolename;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

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

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
}
