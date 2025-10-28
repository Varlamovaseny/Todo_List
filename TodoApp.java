import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Класс задачи
class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String description;
    private boolean completed;
    private java.time.LocalDateTime createdAt;

    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.completed = false;
        this.createdAt = java.time.LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        String status = completed ? "✅" : "⭕";
        String time = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        return String.format("%d. %s %s (%s)", id, status, description, time);
    }
}

// Кастомный рендерер для списка
class TaskListRenderer extends DefaultListCellRenderer {
    private final Color PINK_BACKGROUND = new Color(255, 240, 245);
    private final Color PINK_SELECTION = new Color(255, 182, 193);
    private final Color DARK_PINK = new Color(199, 21, 133);
    private final Color GRAY_TEXT = new Color(128, 128, 128);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof Task) {
            Task task = (Task) value;
            
            if (task.isCompleted()) {
                setFont(getFont().deriveFont(Font.ITALIC));
                setForeground(GRAY_TEXT);
            } else {
                setFont(getFont().deriveFont(Font.BOLD));
                setForeground(DARK_PINK);
            }
            
            if (isSelected) {
                setBackground(PINK_SELECTION);
                setForeground(Color.WHITE);
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(255, 250, 250));
            }
            
            setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }
        
        return this;
    }
}

// Главное приложение
public class TodoApp extends JFrame {
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField taskField;
    private JButton addButton, deleteButton, completeButton;
    private List<Task> tasks;
    private static final String SAVE_FILE = "tasks.dat";
    
    private final Color PINK_BACKGROUND = new Color(255, 240, 245);
    private final Color PINK_BUTTON = new Color(255, 182, 193);
    private final Color PINK_BUTTON_HOVER = new Color(255, 105, 180);
    private final Color PINK_BORDER = new Color(219, 112, 147);
    private final Color DARK_PINK_TEXT = new Color(199, 21, 133);

    public TodoApp() {
        tasks = new ArrayList<>();
        loadTasks();
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        setTitle("🌸 Pink Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PINK_BACKGROUND);

        listModel = new DefaultListModel<>();
        updateListModel();
        
        taskList = new JList<>(listModel);
        taskList.setBackground(Color.WHITE);
        taskList.setSelectionBackground(PINK_BUTTON);
        taskList.setSelectionForeground(Color.WHITE);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskList.setCellRenderer(new TaskListRenderer());

        taskField = new JTextField(20);
        styleTextField(taskField);

        addButton = createPinkButton("➕ Добавить");
        deleteButton = createPinkButton("🗑️ Удалить");
        completeButton = createPinkButton("✅ Выполнено");

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(PINK_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel("Новая задача:");
        label.setForeground(DARK_PINK_TEXT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        inputPanel.add(label);
        inputPanel.add(taskField);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(PINK_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);

        JLabel titleLabel = new JLabel("🌸 Мои Задачи 🌸", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(DARK_PINK_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        titleLabel.setBackground(new Color(255, 228, 225));
        titleLabel.setOpaque(true);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(DARK_PINK_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setCaretColor(PINK_BORDER);
    }

    private JButton createPinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PINK_BUTTON);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PINK_BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PINK_BUTTON);
            }
        });
        
        return button;
    }

    private void setupListeners() {
        addButton.addActionListener(e -> addTask());
        taskField.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> toggleTaskCompletion());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });

        taskList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteTask();
                }
            }
        });
    }

    private void addTask() {
        String description = taskField.getText().trim();
        if (!description.isEmpty()) {
            Task task = new Task(tasks.size() + 1, description);
            tasks.add(task);
            updateListModel();
            taskField.setText("");
            taskField.requestFocus();
            saveTasks();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            updateTaskIds();
            updateListModel();
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите задачу для удаления!", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void toggleTaskCompletion() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = tasks.get(selectedIndex);
            task.setCompleted(!task.isCompleted());
            taskList.repaint();
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите задачу!", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateTaskIds() {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setId(i + 1);
        }
    }

    private void updateListModel() {
        listModel.clear();
        for (Task task : tasks) {
            listModel.addElement(task);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
                tasks = (List<Task>) ois.readObject();
                System.out.println("Задачи загружены!");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка при загрузке задач: " + e.getMessage());
                tasks = new ArrayList<>();
            }
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(tasks);
            System.out.println("Задачи сохранены!");
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении задач: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Упрощаем - убираем установку Look and Feel
        SwingUtilities.invokeLater(() -> {
            new TodoApp().setVisible(true);
        });
    }
}