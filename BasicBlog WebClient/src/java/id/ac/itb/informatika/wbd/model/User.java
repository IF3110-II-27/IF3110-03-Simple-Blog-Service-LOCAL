package id.ac.itb.informatika.wbd.model;

public class User {
    private String id;
    private String email;
    private String name;
    private String password;
    private String role;
    
    public String getId(){
        return id;
    }
    
    public String getEmail(){
        return email;
    }
    
    public String getName(){
        return name;
    }
    
    public String getPassword(){
        return password;
    }
    
    public String getRole(){
        return role;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setRole(String role){
        this.role = role;
    }
}
