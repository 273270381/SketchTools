package ezviz.ezopensdk.been;

public class StyleMap {
    private String id;
    private String styleUrl;

    public StyleMap() {
    }

    public StyleMap(String id, String styleUrl) {
        this.id = id;
        this.styleUrl = styleUrl;
    }

    public String getId() {
        return id;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }
}
