package ddwu.mobile.finalproject.ma02_20201021.model.json;

import java.util.List;

public class BlogRoot {
    private List<NaverBlogDto> items;

    public List<NaverBlogDto> getItems() {
        return items;
    }

    public void setItems(List<NaverBlogDto> items) {
        this.items = items;
    }
}
