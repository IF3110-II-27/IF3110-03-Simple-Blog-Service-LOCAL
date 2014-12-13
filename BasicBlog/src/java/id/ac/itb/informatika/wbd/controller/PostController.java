package id.ac.itb.informatika.wbd.controller;

import id.ac.itb.informatika.wbd.model.Post;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class PostController {
    
    private int id;
    
    private Post post;
    
    private ArrayList<Post> publishedPosts;
    private ArrayList<Post> draftPosts;
    private ArrayList<Post> deletedPosts;
    
    public PostController(){
        post = new Post();
        pos = new Post();
        publishedPosts = new ArrayList<Post>();
        draftPosts = new ArrayList<Post>();
        deletedPosts = new ArrayList<Post>();
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, password);
            Statement sm = con.createStatement();
            ResultSet res = sm.executeQuery("SELECT * FROM post ORDER BY Tanggal DESC, id DESC");
            while(res.next()){
                Post pos = new Post();
                pos.setId(res.getInt("id"));
                pos.setJudul(res.getString("Judul"));
                pos.setKonten(res.getString("Konten"));
                pos.setStatus(res.getString("Status"));
                pos.setTanggal(res.getString("Tanggal"));
                pos.setDeleted(res.getInt("deleted"));
                if(pos.getDeleted()==1){
                    deletedPosts.add(pos);
                }else if(pos.getStatus().equalsIgnoreCase("unpublished")){
                    draftPosts.add(pos);
                }else{
                    publishedPosts.add(pos);
                }
            }
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        finally{
        }
    }

    public int getId(){
        return id;
    }
    public Post getPost(){
        return post;
    }
    public void read(int id){
        this.id = id;
        post.setId(id);
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
                post.setJudul(res.getString("Judul"));
                post.setKonten(res.getString("Konten"));
                post.setStatus(res.getString("Status"));
                post.setTanggal(res.getString("Tanggal"));
            }
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    public void setPost(Post post){
        this.post = new Post();
        this.post.setId(post.getId());
        this.post.setJudul(post.getJudul());
        this.post.setKonten(post.getKonten());
        this.post.setStatus(post.getStatus());
        this.post.setTanggal(post.getTanggal());
    }

    public ArrayList<Post> getDeletedPosts() {
        return deletedPosts;
    }

    public void setDeletedPosts(ArrayList<Post> deletedPosts) {
        this.deletedPosts = deletedPosts;
    }
    
    public ArrayList<Post> getPublishedPosts(){
        return this.publishedPosts;
    }
    
    public ArrayList<Post> getDraftPosts(){
        return this.draftPosts;
    }

    public void setPublishedPosts(ArrayList<Post> publishedPosts) {
        this.publishedPosts = publishedPosts;
    }

    public void setDraftPosts(ArrayList<Post> draftPosts) {
        this.draftPosts = draftPosts;
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
    public void setId(int id){
        this.id = id;
    }
    public void setPos(Post pos){
        this.pos = new Post();
        this.pos.setId(pos.getId());
        this.pos.setJudul(pos.getJudul());
        this.pos.setKonten(pos.getKonten());
        this.pos.setStatus(pos.getStatus());
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
            ps.setInt(4, pos.getId());               
            ps.executeUpdate();
            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            con.close();
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public void execute(int id){
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
                pos.setStatus(res.getString("Status"));
                pos.setTanggal(res.getString("Tanggal"));
                pos.setDeleted(res.getInt("deleted"));
                pos.setId(id);
                this.id = id;
            }            
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
