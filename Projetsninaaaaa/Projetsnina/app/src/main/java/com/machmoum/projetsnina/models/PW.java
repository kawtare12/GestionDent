package com.machmoum.projetsnina.models;


public class PW {

    private long id;

    private long studentid;
    private String title;
    private String objectif;
    private String docs;

    private Tooth tooth;

    private String tooth_nom;

    public PW(long id,long studenid, String title, String objectif,String  tooth) {
        this.id = id;
        this.title = title;
        this.objectif = objectif;
        this.tooth_nom = tooth;
        this.studentid=studenid;
    }

    public PW(int id, long studentId, String title, String objectif) {
        this.id = id;
        this.title = title;
        this.objectif = objectif;
        this.studentid=studentId;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }

    public Tooth getTooth() {
        return tooth;
    }

    public void setTooth(Tooth tooth) {
        this.tooth = tooth;
    }

    public String getTooth_nom() {
        return tooth_nom;
    }

    public void setTooth_nom(String tooth_nom) {
        this.tooth_nom = tooth_nom;
    }

    public long getStudentid() {
        return studentid;
    }

    public void setStudentid(long studentid) {
        this.studentid = studentid;
}
}
