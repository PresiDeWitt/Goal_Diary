package model;



public class Post {
    private int id;
    private int userId;
    private String title;
    private String body;
    private String category;
    private String extractionDate;
    private boolean isLongTitle;

    // Constructor vacío
    public Post() {}

    // Constructor completo
    public Post(int id, int userId, String title, String body, String category, String extractionDate, boolean isLongTitle) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.category = category;
        this.extractionDate = extractionDate;
        this.isLongTitle = isLongTitle;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getExtractionDate() { return extractionDate; }
    public void setExtractionDate(String extractionDate) { this.extractionDate = extractionDate; }

    public boolean isLongTitle() { return isLongTitle; }
    public void setLongTitle(boolean longTitle) { isLongTitle = longTitle; }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", category='" + category + '\'' +
                ", extractionDate='" + extractionDate + '\'' +
                ", isLongTitle=" + isLongTitle +
                '}';
    }
}