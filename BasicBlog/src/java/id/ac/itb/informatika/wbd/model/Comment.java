package id.ac.itb.informatika.wbd.model;

public class Comment {

    private String id;
    private String postId;
    private String name;
    private String date;
    private String content;
    
    public Comment() {
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    public String getId(){
        return id;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDate(){
        return date;
    }
    
    public String getContent(){
        return content;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public void setId(String id){
        this.id = id;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void getEMail(String email){
        this.date = email;
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
