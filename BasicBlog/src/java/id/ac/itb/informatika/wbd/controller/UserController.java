package id.ac.itb.informatika.wbd.controller;

import id.ac.itb.informatika.wbd.model.User;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
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
    
    public void create(String email, String name, String password, String role){
        try{
            Connection con = getConnection();
            String query = "INSERT INTO member (Email, Name, Password, Role) "
                    + "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);            
            ps.setString(2, name);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("user_list.xhtml");
            con.close();
        } catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void update(){
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";
        try {
            Class.forName(driver).newInstance();            
            con = DriverManager.getConnection(url, user, password);
            String query = "UPDATE member SET Email=?, Name=?, Password=?, Role=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, this.user.getEmail());
            ps.setString(2, this.user.getName());            
            ps.setString(3, this.user.getPassword());
            ps.setString(4, this.user.getRole());
            //ps.setInt(5, this.user.getId());               
            ps.executeUpdate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("user_list.xhtml");
            con.close();
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void read(int id){
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";        
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM member WHERE id="+id);
            ResultSet res = ps.executeQuery();
            while(res.next()){
                this.user.setEmail(res.getString("Email"));
                this.user.setName(res.getString("Name"));
                this.user.setPassword(res.getString("Password"));
                this.user.setRole(res.getString("Role"));
                //this.user.setId(id);
                this.id = id;
            }            
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String delete(int id){
        try{
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM member WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            con.close();
        } catch(SQLException e){            
        }
        return "user_list?faces-redirect=true";
    }
}
