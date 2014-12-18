package id.ac.itb.informatika.wbd.model;

import java.sql.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "post")
@RequestScoped
public class Post {
    private String id;
    private String judul;
    private String tanggal;
    private String konten;
    private String published;
    private String deleted;
    
    public Post() {
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
    
    public String getId(){
        return id;
    }
    
    public String getJudul(){
        return judul;
    }
    
    public String getTanggal(){
        return tanggal;
    }
    
    public String getKonten(){
        return konten;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public void setJudul(String judul){
        this.judul = judul;
    }
    
    public void setTanggal(String tanggal){
        this.tanggal = tanggal;
    }
    
    public void setKonten(String konten){
        this.konten = konten;
    }
    
    public String printTanggal(){
        int dd = Integer.parseInt(tanggal.substring(8,10));
        String s =  dd + " ";
        int mm = Integer.parseInt(tanggal.substring(5, 7));
        String m;
        switch(mm){
            case 1: m="Januari"; break;
            case 2: m="Februari"; break;
            case 3: m="Maret"; break;
            case 4: m="April"; break;
            case 5: m="Mei"; break;
            case 6: m="Juni"; break;
            case 7: m="Juli"; break;
            case 8: m="Agustus"; break;
            case 9: m="September"; break;
            case 10: m="Oktober"; break;
            case 11: m="November"; break;
            default: m="Desember"; break;
        }
        s = s + m + " " + (tanggal.substring(0,4));
        
        return s;
    }
}
