package id.ac.itb.informatika.wbd.model;

public class Post {
    private String id;
    private String title;
    private String date;
    private String content;
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
    
    public String getTitle(){
        return title;
    }
    
    public String getDate(){
        return date;
    }
    
    public String getContent(){
        return content;
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
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public void setDate(String date){
        this.date = date;
    }
    
    public void setContent(String content){
        this.content = content;
    }
    
    public String printTanggal(){
        int dd = Integer.parseInt(date.substring(8,10));
        String s =  dd + " ";
        int mm = Integer.parseInt(date.substring(5, 7));
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
        s = s + m + " " + (date.substring(0,4));
        
        return s;
    }
}
