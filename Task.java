
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String description;
    private String tag;
    private String deadline;
    private boolean completed;
    private LocalDateTime createdAt;

    public Task(int id, String title, String description, String tag, String deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.deadline = deadline;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        String status = completed ? "[✓]" : "[ ]";
        String time = createdAt.format(DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d. %s %s", id, status, title));
        
        if (tag != null && !tag.isEmpty()) {
            sb.append(" [").append(tag).append("]");
        }
        
        if (deadline != null && !deadline.isEmpty()) {
            sb.append(" 📅 ").append(deadline);
        }
        
        if (description != null && !description.isEmpty()) {
            sb.append(" - ").append(description);
        }
        
        sb.append(" (").append(time).append(")");
        return sb.toString();
    }
}
