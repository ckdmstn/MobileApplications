package ddwu.mobile.finalproject.ma02_20201021.model.json;

import java.util.List;

public class SearchRoot {
    private List<NaverSearchDto> items;

    public List<NaverSearchDto> getItems() {
        return items;
    }

    public void setItems(List<NaverSearchDto> items) {
        this.items = items;
    }
}
