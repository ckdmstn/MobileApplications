package ddwu.mobile.finalproject.ma02_20201021.model.json;

public class NaverSearchDto {
    private int _id;
    private String title;
    private String link;
    private String category;
    private String roadAddress;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    @Override
    public String toString() {
        return "{" +
                "title='" + title + '\'' +
                ", link='" + link + "\'" +
                ", category='" + category + '\'' +
                ", roadAddress='" + roadAddress + '\'' +
                '}';
    }
}
