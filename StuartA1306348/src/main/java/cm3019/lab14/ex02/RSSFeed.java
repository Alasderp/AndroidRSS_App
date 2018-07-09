package cm3019.lab14.ex02;

/**
 * Created by Alasdairp on 19/04/2016.
 */
public class RSSFeed {

    private String code;
    private String description;

    public RSSFeed(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
