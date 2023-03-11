package ddwu.mobile.finalproject.ma02_20201021;

public class Blog {
    private long _id;
    private String title;
    private String description;
    private String date;
    private String link;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Blog(long _id, String title, String description, String date, String link) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
    }
}
