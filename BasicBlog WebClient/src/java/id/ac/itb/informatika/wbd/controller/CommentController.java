package id.ac.itb.informatika.wbd.controller;

import com.firebase.client.Firebase;
import id.ac.itb.informatika.wbd.helper.DateHelper;
import id.ac.itb.informatika.wbd.model.Comment;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.ws.rs.client.ClientBuilder;
import org.json.JSONObject;

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
    
    public void create(String name, String email, String komentar){
        Comment newComment = new Comment();
        newComment.setPostId(postId);
        newComment.setName(name);
        newComment.setContent(komentar);
        newComment.setDate(new DateHelper().getCDate());
        
        Firebase fb = new Firebase("https://if3110-iii-27.firebaseio.com/");
        fb.child("comments").push().setValue(newComment);
    }

    public void createByUser(String id, String komentar){
        UserController user = new UserController();
        user.read(id);
        create(user.getUser().getName(), user.getUser().getEmail(), komentar);
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    
    public ArrayList<Comment> list(String id){
        Comment comment;
        comments = new ArrayList<Comment>();
        
        String response = ClientBuilder.newClient()
                .target("https://if3110-iii-27.firebaseio.com/")
                .path("comments.json")
                .request()
                .get(String.class);

        try {
            JSONObject json = new JSONObject(response);
            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject val = json.getJSONObject(key);

                comment = new Comment();
                comment.setId(key);
                comment.setName(val.getString("name"));
                comment.setContent(val.getString("content"));
                comment.setDate(val.getString("date"));
                comment.setPostId(val.getString("postId"));

                if (id.equals(comment.getPostId())) {
                    comments.add(comment);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CommentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return comments;
    }
}
