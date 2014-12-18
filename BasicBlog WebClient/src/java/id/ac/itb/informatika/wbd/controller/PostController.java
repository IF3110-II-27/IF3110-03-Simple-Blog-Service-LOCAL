package id.ac.itb.informatika.wbd.controller;

import com.firebase.client.Firebase;
import id.ac.itb.informatika.wbd.model.Post;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
import org.json.JSONObject;

@ManagedBean
@ViewScoped
public class PostController {
    
    private String id;
    
    private Post post;
    
    private ArrayList<Post> publishedPosts;
    private ArrayList<Post> draftPosts;
    private ArrayList<Post> deletedPosts;
    
    public PostController(){
        publishedPosts = new ArrayList<>();
        draftPosts = new ArrayList<>();
        deletedPosts = new ArrayList<>();
        post = new Post();
    }
    
    public void list() {
        publishedPosts = new ArrayList<>();
        draftPosts = new ArrayList<>();
        deletedPosts = new ArrayList<>();
        
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("posts.json?orderByChild=\"date\"")
                .request()
                .get(String.class);

        try {
            JSONObject json = new JSONObject(response);
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject val = json.getJSONObject(key);

                post = new Post();
                post.setId(key);
                post.setTitle(val.getString("title"));
                post.setContent(val.getString("content"));
                post.setPublished(val.getString("published"));
                post.setDate(val.getString("date"));
                post.setDeleted(val.getString("deleted"));

                if(post.getDeleted().equalsIgnoreCase("true")){
                    deletedPosts.add(post);
                }else if(post.getPublished().equalsIgnoreCase("true")){
                    publishedPosts.add(post);
                }else{
                    draftPosts.add(post);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CommentController.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public String create(String judul, String tanggal, String konten) {
        Post newPost = new Post();
        newPost.setTitle(judul);
        newPost.setDate(tanggal);
        newPost.setContent(konten);
        newPost.setPublished("false");
        newPost.setDeleted("false");
        
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("posts").push().setValue(newPost);
        
        return "index?faces-redirect=true";
    }
    
    public String delete(String id, int index){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<>();
        val.put("deleted", "true");
        fb.child("posts").child(id).updateChildren(val);
        
        return index == 1 ? "index?faces-redirect=true" : "post_draft?faces-redirect=true";
    }
    
    public String restore(String id){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<>();
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
        Map<String, Object> val = new HashMap<>();
        val.put("published", "true");
        fb.child("posts").child(id).updateChildren(val);
        
        return "post_draft?faces-redirect=true";
    }
    
    public String update(){
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        Map<String, Object> val = new HashMap<>();
        val.put("title", post.getTitle());
        val.put("date", post.getDate());
        val.put("content", post.getContent());
        fb.child("posts").child(id).updateChildren(val);
        
        return "index?faces-redirect=true";
    }
}
