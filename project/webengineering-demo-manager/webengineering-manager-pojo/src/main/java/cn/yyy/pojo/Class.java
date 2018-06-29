package cn.yyy.pojo;

public class Class {
    private Integer classid;

    private String classname;

    private Integer courseid;

    private Integer personnum;
    
    public Class() {
    	
    }
    
    public Class(Integer classid, String classname, Integer courseid, Integer personnum) {
    	this.setClassid(classid);
    	this.setClassname(classname);
    	this.setCourseid(courseid);
    	this.setPersonnum(personnum);
    }

    public Integer getClassid() {
        return classid;
    }

    public void setClassid(Integer classid) {
        this.classid = classid;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }

    public Integer getCourseid() {
        return courseid;
    }

    public void setCourseid(Integer courseid) {
        this.courseid = courseid;
    }

    public Integer getPersonnum() {
        return personnum;
    }

    public void setPersonnum(Integer personnum) {
        this.personnum = personnum;
    }
}