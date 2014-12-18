package id.ac.itb.informatika.wbd.controller;

import id.ac.itb.informatika.wbd.model.User;
import java.util.ArrayList;
import java.util.Iterator;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ManagedBean(name = "loginController")
@RequestScoped
public class LoginController {
    private String email;
    private String password;
    private String role;
    private String id;
    
    public LoginController(){
        checkCookie();
    }
    
    public String getEmail(){
        return email;
    }
    
    public String getPassword(){
        return password;
    }
    
    public String getRole(){
        return role;
    }
   
    public String getId(){
        return id;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setRole(String role){
        this.role = role;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public Boolean checkUser(){
        UserController uc = new UserController();
        uc.list();
        ArrayList<User> users = uc.getUsers();
        
        for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
            User user = iterator.next();
            if (email.equalsIgnoreCase(user.getEmail()) && password.equals(user.getPassword())) {
                id = user.getId();
                email = user.getEmail();
                password = user.getPassword();
                role = user.getRole();
                return true;
            }
        }
        
        return false;
    }
    
    public String startLogin(){
        if(checkUser()){
            FacesContext facesContext = FacesContext.getCurrentInstance();
            
            Cookie cEmail = new Cookie("cEmail", email);
            Cookie cPassword = new Cookie("cPassword", password);
            Cookie cRole = new Cookie("cRole", role);
            Cookie cId = new Cookie("cId", id);
            
            cEmail.setMaxAge(86400);
            cPassword.setMaxAge(86400);
            cRole.setMaxAge(86400);
            cId.setMaxAge(86400);
		  
            ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cEmail);
            ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cPassword);
            ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cRole);
            ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cId);
            return "success";
        }
        else{
            return "failure";
        }
    }
    
    public String startLogout(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        
        email = "";
        password = "";
        role = "";
        id = "";
        
        Cookie cEmail = new Cookie("cEmail", email);
        Cookie cPassword = new Cookie("cPassword", password);
        Cookie cRole = new Cookie("cRole", role);
        Cookie cId = new Cookie("cId", id);
        
        cEmail.setMaxAge(0);
        cPassword.setMaxAge(0);
        cRole.setMaxAge(0);
        cId.setMaxAge(0);
		  
        ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cEmail);
        ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cPassword);
        ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cRole);
        ((HttpServletResponse)facesContext.getExternalContext().getResponse()).addCookie(cId);
        
        return "logout";
    }
    
    public void checkCookie(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String cookieName = null;
        Cookie cookie[] = ((HttpServletRequest)facesContext.getExternalContext().getRequest()).getCookies();
        if(cookie != null && cookie.length > 0){
            for(int i = 0; i<cookie.length; i++){
                cookieName = cookie[i].getName();
                if(cookieName.equals("cEmail")){
                    email = cookie[i].getValue();
                }
                else if(cookieName.equals("cPassword")){
                    password = cookie[i].getValue();
                }
                else if(cookieName.equals("cRole")){
                    role = cookie[i].getValue();
                }
            }
        }
        else{
            System.out.println("Cannot find any cookie");
        }
    }
}
