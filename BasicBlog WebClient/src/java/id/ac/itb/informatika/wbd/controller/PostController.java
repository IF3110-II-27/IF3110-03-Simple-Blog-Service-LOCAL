package id.ac.itb.informatika.wbd.controller;

import com.firebase.client.Firebase;
import id.ac.itb.informatika.wbd.model.Post;
import id.ac.itb.informatika.wbd.service.BasicBlogServiceImplService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.client.ClientBuilder;
import javax.xml.ws.WebServiceRef;
import org.json.JSONObject;

@ManagedBean
@ViewScoped
public class PostController {
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/if3110-iii-27.herokuapp.com/BasicBlog.wsdl")
    private BasicBlogServiceImplService service;
    
    private String id;
    
    private Post post;
    
    private ArrayList<Post> publishedPosts;
    private ArrayList<Post> draftPosts;
    private ArrayList<Post> deletedPosts;
    private ArrayList<Post> queryPosts;
    
    private String query;
    
    public PostController(){
        publishedPosts = new ArrayList<>();
        draftPosts = new ArrayList<>();
        deletedPosts = new ArrayList<>();
        queryPosts = new ArrayList<>();
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
            Logger.getLogger(PostController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void search(String query) {
        if (!query.isEmpty()) {
            list();
            for (Iterator<Post> iterator = publishedPosts.iterator(); iterator.hasNext();) {
                Post next = iterator.next();
                if (next.getContent().contains(query) || next.getTitle().contains(query)) {
                    queryPosts.add(next);
                }
            }
        }
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
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

    public ArrayList<Post> getQueryPosts() {
        return queryPosts;
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

    private java.util.List<id.ac.itb.informatika.wbd.service.Post> listPost() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.listPost();
    }

    private Boolean addPost(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.addPost(arg0, arg1, arg2);
    }

    private Boolean deletePost(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.deletePost(arg0);
    }

    private Boolean deletePostPermanent(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.deletePostPermanent(arg0);
    }

    private Boolean editPost(java.lang.String arg0, java.lang.String arg1, java.lang.String arg2, java.lang.String arg3) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.editPost(arg0, arg1, arg2, arg3);
    }

    private Boolean publishPost(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.publishPost(arg0);
    }

    private Boolean restorePost(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.restorePost(arg0);
    }

    private java.util.List<id.ac.itb.informatika.wbd.service.Post> search_1(java.lang.String arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        id.ac.itb.informatika.wbd.service.BasicBlogService port = service.getBasicBlogServiceImplPort();
        return port.search(arg0);
    }

}
