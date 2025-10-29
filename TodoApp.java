import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private java.time.LocalDateTime createdAt;

    public Task(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.createdAt = java.time.LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        String status = completed ? "[✓]" : "[ ]";
        String time = createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM HH:mm"));
        
        if (description == null || description.isEmpty()) {
            return String.format("%d. %s %s (%s)", id, status, title, time);
        } else {
            return String.format("%d. %s %s - %s (%s)", id, status, title, description, time);
        }
    }
}

class TaskListRenderer extends DefaultListCellRenderer {
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

public class TodoApp extends JFrame {
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton addButton, deleteButton, completeButton, editButton;
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
        setTitle("Pink Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PINK_BACKGROUND);

        listModel = new DefaultListModel<>();
        updateListModel();
        
        taskList = new JList<>(listModel);
        taskList.setBackground(Color.WHITE);
        taskList.setSelectionBackground(PINK_BUTTON);
        taskList.setSelectionForeground(Color.WHITE);
        taskList.setCellRenderer(new TaskListRenderer());

        titleField = new JTextField(20);
        styleTextField(titleField);
        
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setForeground(DARK_PINK_TEXT);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        addButton = createPinkButton("Добавить");
        deleteButton = createPinkButton("Удалить");
        completeButton = createPinkButton("Выполнено");
        editButton = createPinkButton("Редактировать");

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(PINK_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PINK_BACKGROUND);
        JLabel titleLabel = new JLabel("Заголовок:");
        titleLabel.setForeground(DARK_PINK_TEXT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(PINK_BACKGROUND);
        JLabel descLabel = new JLabel("Описание:");
        descLabel.setForeground(DARK_PINK_TEXT);
        descLabel.setFont(descLabel.getFont().deriveFont(Font.BOLD));
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        JPanel buttonPanelTop = new JPanel(new FlowLayout());
        buttonPanelTop.setBackground(PINK_BACKGROUND);
        buttonPanelTop.add(addButton);
        
        inputPanel.add(titlePanel, BorderLayout.NORTH);
        inputPanel.add(descPanel, BorderLayout.CENTER);
        inputPanel.add(buttonPanelTop, BorderLayout.SOUTH);

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(PINK_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(deleteButton);
        controlPanel.add(completeButton);
        controlPanel.add(editButton);

        JLabel titleLabelMain = new JLabel("Мои Задачи", JLabel.CENTER);
        titleLabelMain.setFont(titleLabelMain.getFont().deriveFont(Font.BOLD, 20));
        titleLabelMain.setForeground(DARK_PINK_TEXT);
        titleLabelMain.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        titleLabelMain.setBackground(new Color(255, 228, 225));
        titleLabelMain.setOpaque(true);

        setLayout(new BorderLayout());
        add(titleLabelMain, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(DARK_PINK_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setCaretColor(PINK_BORDER);
    }

    private JButton createPinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        button.setForeground(Color.WHITE);
        button.setBackground(PINK_BUTTON);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
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
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> toggleTaskCompletion());
        editButton.addActionListener(e -> editTask());

        titleField.addActionListener(e -> addTask());

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

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTask();
                }
            }
        });
    }

    private void addTask() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        if (!title.isEmpty()) {
            Task task = new Task(tasks.size() + 1, title, description);
            tasks.add(task);
            updateListModel();
            titleField.setText("");
            descriptionArea.setText("");
            titleField.requestFocus();
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Введите заголовок задачи!", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Удалить выбранную задачу?", "Подтверждение удаления", 
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                tasks.remove(selectedIndex);
                updateTaskIds();
                updateListModel();
                saveTasks();
            }
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

    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = tasks.get(selectedIndex);
            
            JTextField editTitleField = new JTextField(task.getTitle(), 20);
            JTextArea editDescArea = new JTextArea(task.getDescription(), 3, 20);
            editDescArea.setLineWrap(true);
            editDescArea.setWrapStyleWord(true);
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            JLabel titleLabel = new JLabel("Заголовок:");
            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(editTitleField, BorderLayout.CENTER);
            
            JLabel descLabel = new JLabel("Описание:");
            panel.add(descLabel, BorderLayout.SOUTH);
            panel.add(new JScrollPane(editDescArea), BorderLayout.AFTER_LAST_LINE);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Редактирование задачи", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String newTitle = editTitleField.getText().trim();
                if (!newTitle.isEmpty()) {
                    task.setTitle(newTitle);
                    task.setDescription(editDescArea.getText().trim());
                    updateListModel();
                    saveTasks();
                } else {
                    JOptionPane.showMessageDialog(this, "Заголовок не может быть пустым!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите задачу для редактирования!", "Информация", JOptionPane.INFORMATION_MESSAGE);
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
            } catch (IOException | ClassNotFoundException e) {
                tasks = new ArrayList<>();
            }
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TodoApp().setVisible(true);
        });
    }
}