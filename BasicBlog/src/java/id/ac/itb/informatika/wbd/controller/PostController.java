package id.ac.itb.informatika.wbd.controller;

import com.firebase.client.Firebase;
import id.ac.itb.informatika.wbd.model.Post;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;

@ManagedBean
@ViewScoped
public class PostController {
    
    private String id;
    
    private Post post;
    
    private ArrayList<Post> publishedPosts;
    private ArrayList<Post> draftPosts;
    private ArrayList<Post> deletedPosts;
    
    private String debug = "";
    
    public String getDebug() {
        return debug;
    }
    
    public PostController(){
        post = new Post();
        
        publishedPosts = new ArrayList<>();
        draftPosts = new ArrayList<>();
        deletedPosts = new ArrayList<>();
        
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("posts.json?orderByChild=\"date\"")
                .request()
                .get(String.class);

        JSONObject json = new JSONObject(response);
        Iterator<String> keys = json.keys();
        
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject val = json.getJSONObject(key);
            
            pos = new Post();
            pos.setId(key);
            pos.setTitle(val.getString("title"));
            pos.setContent(val.getString("content"));
            pos.setPublished(val.getString("published"));
            pos.setDate(val.getString("date"));
            pos.setDeleted(val.getString("deleted"));
            
            if(pos.getDeleted().equalsIgnoreCase("true")){
                deletedPosts.add(pos);
            }else if(pos.getPublished().equalsIgnoreCase("true")){
                publishedPosts.add(pos);
            }else{
                draftPosts.add(pos);
            }
        }
    }

    public String getId(){
        return id;
    }
    public Post getPost(){
        return post;
    }
    public void read(String id){
        this.id = id;
        post.setId(id);
        
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("posts/" + id + ".json")
                .request()
                .get(String.class);
        
        JSONObject json = new JSONObject(response);
        
        post.setTitle(json.getString("title"));
        post.setContent(json.getString("content"));
        post.setDate(json.getString("date"));
    }
    
    public void setPost(Post post){
        this.post = new Post();
        this.post.setId(post.getId());
        this.post.setTitle(post.getTitle());
        this.post.setContent(post.getContent());
        this.post.setPublished(post.getPublished());
        this.post.setDate(post.getDate());
        this.post.setDeleted(post.getDeleted());
    }

    public ArrayList<Post> getDeletedPosts() {
        return deletedPosts;
    }
    
    public ArrayList<Post> getPublishedPosts(){
        return this.publishedPosts;
    }
    
    public ArrayList<Post> getDraftPosts(){
        return this.draftPosts;
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
    
    public void create(String judul, String tanggal, String konten) {
        Post newPost = new Post();
        newPost.setTitle(judul);
        newPost.setDate(tanggal);
        newPost.setContent(konten);
        newPost.setPublished("false");
        newPost.setDeleted("false");
        
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("posts").push().setValue(newPost);
        
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        } catch (IOException ex) {
            Logger.getLogger(PostController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String delete(String id, int index){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("deleted", "true");
        fb.child("posts").child(id).updateChildren(val);
        
        return index == 1 ? "index?faces-redirect=true" : "post_draft?faces-redirect=true";
    }
    
    public String restore(String id){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("deleted", "false");
        fb.child("posts").child(id).updateChildren(val);
        
        return "post_deleted?faces-redirect=true";
    }
    
    public String deletePermanent(String id){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("posts").child(id).removeValue();
        
        return "post_deleted?faces-redirect=true";
    }
    
    public String publish(String id){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<String, Object>();
        val.put("published", "true");
        fb.child("posts").child(id).updateChildren(val);
        
        return "post_draft?faces-redirect=true";
    }
    
   private Post pos;
   
    public Post getPos(){
        return pos;
    }

    public void setPos(Post pos){
        this.pos = new Post();
        this.pos.setId(pos.getId());
        this.pos.setTitle(pos.getTitle());
        this.pos.setContent(pos.getContent());
        this.pos.setPublished(pos.getPublished());
        this.pos.setDate(pos.getDate());
    }
    
    public void edit(){
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";
        try {
            Class.forName(driver).newInstance();            
            con = DriverManager.getConnection(url, user, password);
            String query = "UPDATE post SET Judul=?, Konten=?, Tanggal=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, pos.getTitle());
            ps.setString(2, pos.getContent());            
            ps.setString(3, pos.getDate());
           // ps.setInt(4, pos.getId());               
            ps.executeUpdate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            con.close();
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void execute(String id){
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";        
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, password);
            PreparedStatement ps = con.prepareStatement("SELECT * FROM post WHERE id="+id);
            ResultSet res = ps.executeQuery();
            while(res.next()){
                pos.setTitle(res.getString("Judul"));
                pos.setContent(res.getString("Konten"));
                //pos.setStatus(res.getString("Status"));
                pos.setDate(res.getString("Tanggal"));
                //pos.setDeleted(res.getInt("deleted"));
                //pos.setId(id);
                this.id = id;
            }            
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
