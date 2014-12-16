package id.ac.itb.informatika.wbd.controller;

import id.ac.itb.informatika.wbd.model.Post;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
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
            pos.setJudul(val.getString("title"));
            pos.setKonten(val.getString("content"));
            pos.setPublished(val.getBoolean("published"));
            pos.setTanggal(val.getString("date"));
            pos.setDeleted(val.getBoolean("deleted"));
            
            if(pos.getDeleted()){
                deletedPosts.add(pos);
            }else if(pos.getPublished()){
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
        
        post.setJudul(json.getString("title"));
        post.setKonten(json.getString("content"));
        post.setPublished(json.getBoolean("published"));
        post.setTanggal(json.getString("date"));
        post.setDeleted(json.getBoolean("deleted"));
    }
    
    public void setPost(Post post){
        this.post = new Post();
        this.post.setId(post.getId());
        this.post.setJudul(post.getJudul());
        this.post.setKonten(post.getKonten());
        this.post.setPublished(post.getPublished());
        this.post.setTanggal(post.getTanggal());
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
    
    public void create(String judul, String tanggal, String konten){
        try{
            Connection con = getConnection();
            String query = "INSERT INTO post (id_member, Status,Judul,Konten,Tanggal, deleted) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, 1);            
            ps.setString(2, "unpublished");
            ps.setString(3, judul);
            ps.setString(4, konten);
            ps.setString(5, tanggal);  
            ps.setInt(6, 0);
            ps.executeUpdate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            con.close();
        } catch (IOException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String delete(int id, int index){
        try{
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE post SET deleted=? WHERE id=?");
            ps.setInt(1, 1);
            ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
        } catch(SQLException e){            
        }
        if(index == 1)
            return "index?faces-redirect=true";
        else
            return "post_draft?faces-redirect=true";
    }
    
    public String restore(int id){
        try{
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE post SET deleted=? WHERE id=?");
            ps.setInt(1, 0);
            ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
        } catch(SQLException e){            
        }
        return "post_deleted?faces-redirect=true";
    }
    
    public String deletePermanent(int id){
        try{
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("DELETE FROM post WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            con.close();
        } catch(SQLException e){            
        }
        return "post_deleted?faces-redirect=true";
    }
    
    public String publish(int id){
        try{
            Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE post SET status=? WHERE id=?");
            ps.setString(1, "published");
            ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
        } catch(SQLException e){            
        }
        return "post_draft?faces-redirect=true";
    }
    
   private Post pos;
   
    public Post getPos(){
        return pos;
    }

    public void setPos(Post pos){
        this.pos = new Post();
        this.pos.setId(pos.getId());
        this.pos.setJudul(pos.getJudul());
        this.pos.setKonten(pos.getKonten());
        this.pos.setPublished(pos.getPublished());
        this.pos.setTanggal(pos.getTanggal());
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
            ps.setString(1, pos.getJudul());
            ps.setString(2, pos.getKonten());            
            ps.setString(3, pos.getTanggal());
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
                pos.setJudul(res.getString("Judul"));
                pos.setKonten(res.getString("Konten"));
                //pos.setStatus(res.getString("Status"));
                pos.setTanggal(res.getString("Tanggal"));
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
