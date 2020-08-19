package ezviz.ezopensdk.been;

public class StyleId {
    private String id;
    private String LineColor;
    private String LineWidth;

    public StyleId() {
    }

    public StyleId(String id, String lineColor, String lineWidth) {
        this.id = id;
        LineColor = lineColor;
        LineWidth = lineWidth;
    }

    public String getId() {
        return id;
    }

    public String getLineColor() {
        return LineColor;
    }

    public String getLineWidth() {
        return LineWidth;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLineColor(String lineColor) {
        LineColor = lineColor;
    }

    public void setLineWidth(String lineWidth) {
        LineWidth = lineWidth;
    }
}
