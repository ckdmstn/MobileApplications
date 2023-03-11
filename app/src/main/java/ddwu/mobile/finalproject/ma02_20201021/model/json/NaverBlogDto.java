package ddwu.mobile.finalproject.ma02_20201021.model.json;

public class NaverBlogDto {
    private int _id;
    private String title;
    private String description;
    private String link;
    private String postdate;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public NaverBlogDto(int _id, String title, String description, String link, String postdate) {
        this._id = _id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.postdate = postdate;
    }
}
