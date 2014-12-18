package id.ac.itb.informatika.wbd.controller;

import com.firebase.client.Firebase;
import id.ac.itb.informatika.wbd.model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
import org.json.JSONObject;

@ManagedBean
@ViewScoped
public class UserController {
    private int id;
    private User user;
    private ArrayList<User> users;
    
    public UserController(){
        user = new User();
        users = new ArrayList<User>();
       
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("users.json")
                .request()
                .get(String.class);

        JSONObject json = new JSONObject(response);
        Iterator<String> keys = json.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject val = json.getJSONObject(key);
            user = new User();
            user.setId(key);
            user.setEmail(val.getString("email"));
            user.setName(val.getString("name"));
            user.setPassword(val.getString("password"));
            user.setRole(val.getString("role"));
            
            users.add(user);
        }
    }
    
    public int getId(){
        return id;
    }
    public User getUser(){
        return user;
    }
    public ArrayList<User> getMembers(){
        return users;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setUser(User user){
        this.user = new User();
        this.user.setId(user.getId());
        this.user.setEmail(user.getEmail());
        this.user.setName(user.getName());
        this.user.setPassword(user.getPassword());
        this.user.setRole(user.getRole());
    }
    public Connection getConnection() throws SQLException{
        Connection con = null;

        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection completed.");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        finally{
        }
        return con;
    }
    
    public void create(String name, String email, String password, String role){
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role);
        
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("users").push().setValue(newUser);
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("user_list.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void update(){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<>();
        val.put("name", user.getName());
        val.put("email", user.getEmail());
        val.put("password", user.getPassword());
        val.put("role", user.getRole());
        fb.child("users").child(user.getId()).updateChildren(val);
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("user_list.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void read(String id){
        user.setId(id);
        
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("users/" + id + ".json")
                .request()
                .get(String.class);
        
        JSONObject json = new JSONObject(response);
        
        user.setName(json.getString("name"));
        user.setEmail(json.getString("email"));
        user.setPassword(json.getString("password"));
        user.setRole(json.getString("role"));
    }
    
    public String delete(String id){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("users").child(id).removeValue();
        
        return "user_list?faces-redirect=true";
    }
}
