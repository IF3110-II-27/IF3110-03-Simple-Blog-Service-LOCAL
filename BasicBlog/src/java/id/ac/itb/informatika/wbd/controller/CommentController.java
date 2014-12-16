package id.ac.itb.informatika.wbd.controller;

import id.ac.itb.informatika.wbd.helper.DateHelper;
import id.ac.itb.informatika.wbd.model.Comment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class CommentController {
    
    private ArrayList<Comment> comments;
    
    public CommentController(){        
    }
    
    private String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        finally{
        }
        return con;
    }
    
    public void create(String name, String email, String komentar){
        try{
            Connection con = getConnection();
            String query = "INSERT INTO comment (id_post, Nama, Email, Tanggal, Komentar) "
                    + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            //ps.setInt(1, postId);
            ps.setString(2, name);
            ps.setString(3, email);
            DateHelper cd = new DateHelper();
            ps.setString(4, cd.getCDate());
            ps.setString(5, komentar);
            ps.executeUpdate();
            //FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void createByUser(String id, String komentar){
        try {
            Connection con = getConnection();
            Statement sm = con.createStatement();
            ResultSet res = sm.executeQuery("SELECT * FROM member WHERE id="+ Integer.parseInt(id));
            String email="";
            String nama="";
            while(res.next()){
                email = res.getString("Email");
                nama = res.getString("Name");
            }
            con.close();
            create(nama, email, komentar);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    
    public ArrayList<Comment> read(String id){
        comments = new ArrayList<Comment>();
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/simpleblog";
        String user = "root";
        String driver = "com.mysql.jdbc.Driver";
        String password = "";
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, password);
            Statement sm = con.createStatement();
            ResultSet res = sm.executeQuery("SELECT * FROM comment WHERE id_post="+id+" ORDER BY Tanggal DESC, id DESC");
            while(res.next()){
                Comment com = new Comment();
                com.setId(res.getInt("id"));
                //com.setIdPost(id);
                com.setEmail(res.getString("Email"));
                com.setNama(res.getString("Nama"));
                com.setTanggal(res.getString("Tanggal"));
                com.setKomentar(res.getString("Komentar"));
                comments.add(com);
            }
            con.close();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return comments;
    }
}
