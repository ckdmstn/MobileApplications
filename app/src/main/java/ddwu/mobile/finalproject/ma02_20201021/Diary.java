package ddwu.mobile.finalproject.ma02_20201021;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"address"}, unique = true)})
public class Diary implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo
    public String title;

    @ColumnInfo
    public String address;

    @ColumnInfo
    public String content;

    @ColumnInfo
    public String dateTime;

    public Diary() {
    }

    public Diary(String title, String address, String content) {
        this.title = title;
        this.address = address;
        this.content = content;
    }

    public Diary(int id, String title, String address, String content) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
