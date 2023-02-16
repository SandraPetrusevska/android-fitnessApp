public class Training {
    private String name;
    private String capacity;
    private String date;
    private String imgURL;

    public Training(String name, String capacity,  String date, String imgURL) {
        this.capacity = capacity;
        this.name = name;
        this.date = date;
        this.imgURL = imgURL;
    }
    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
