import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Task {
    private int id;
    private String description;
    private boolean completed;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        String status = completed ? "✓" : "◯";
        return String.format("%d. %s %s", id, status, description);
    }
}

class TaskListRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                  boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Task) {
            Task task = (Task) value;
            
            if (task.isCompleted()) {
                setFont(getFont().deriveFont(Font.ITALIC));
                setForeground(Color.GRAY);
            } else {
                setFont(getFont().deriveFont(Font.PLAIN));
                setForeground(Color.BLACK);
            }
        }
        
        return c;
    }
}

public class TodoApp extends JFrame {
    private List<Task> tasks;
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField taskField;

    public TodoApp() {
        tasks = new ArrayList<>();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Todo Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskListRenderer());

        taskField = new JTextField(20);
        JButton addButton = new JButton("Добавить");
        JButton deleteButton = new JButton("Удалить");
        JButton toggleButton = new JButton("Выполнено/Не выполнено");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Новая задача:"));
        inputPanel.add(taskField);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(toggleButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        addButton.addActionListener(e -> addTask());
        taskField.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        toggleButton.addActionListener(e -> toggleTask());
    }

    private void addTask() {
        String description = taskField.getText().trim();
        if (!description.isEmpty()) {
            Task task = new Task(tasks.size() + 1, description);
            tasks.add(task);
            listModel.addElement(task);
            taskField.setText("");
            taskField.requestFocus();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            listModel.remove(selectedIndex);
            updateTaskIds();
        }
    }

    private void toggleTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = tasks.get(selectedIndex);
            task.setCompleted(!task.isCompleted());
            taskList.repaint();
        }
    }

    private void updateTaskIds() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setId(i + 1);
        }
        taskList.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TodoApp().setVisible(true);
        });
    }
}