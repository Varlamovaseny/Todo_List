import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class DatePickerPanel extends JPanel {
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    
    public DatePickerPanel() {
        setLayout(new FlowLayout());
        
       
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }
        
 
        monthCombo = new JComboBox<>(new String[]{
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        });
        

        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        

        setToToday();
        
        add(new JLabel("День:"));
        add(dayCombo);
        add(new JLabel("Месяц:"));
        add(monthCombo);
        add(new JLabel("Год:"));
        add(yearCombo);
    }
    
    public String getDate() {
        int day = (Integer) dayCombo.getSelectedItem();
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearCombo.getSelectedItem();
        
        try {
            LocalDate date = LocalDate.of(year, month, day);
            return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return "";
        }
    }
    
    public void setDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            setToToday();
            return;
        }
        
        try {
            DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd.MM.yy"),
                DateTimeFormatter.ofPattern("dd.MM")
            };
            
            LocalDate date = null;
            for (DateTimeFormatter formatter : formatters) {
                try {
                    date = LocalDate.parse(dateStr, formatter);
                    break;
                } catch (Exception e) {
                }
            }
            
            if (date != null) {
                dayCombo.setSelectedItem(date.getDayOfMonth());
                monthCombo.setSelectedIndex(date.getMonthValue() - 1);
                yearCombo.setSelectedItem(date.getYear());
            } else {
                setToToday();
            }
        } catch (Exception e) {
            setToToday();
        }
    }
    
    public void setToToday() {
        LocalDate today = LocalDate.now();
        dayCombo.setSelectedItem(today.getDayOfMonth());
        monthCombo.setSelectedIndex(today.getMonthValue() - 1);
        yearCombo.setSelectedItem(today.getYear());
    }
}

public class TodoApp extends JFrame {
    private DefaultListModel<Task> listModel;
    private JList<Task> taskList;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> tagComboBox;
    private DatePickerPanel datePicker;
    private JButton addButton, deleteButton, completeButton, editButton;
    private List<Task> tasks;
    private static final String SAVE_FILE = "tasks.dat";
    
    private final Color PINK_BACKGROUND = new Color(255, 240, 245);
    private final Color PINK_BUTTON = new Color(255, 182, 193);
    private final Color PINK_BUTTON_HOVER = new Color(255, 105, 180);
    private final Color PINK_BORDER = new Color(219, 112, 147);
    private final Color DARK_PINK = new Color(199, 21, 133); // Исправлено: DARK_PINK_TEXT на DARK_PINK

    public TodoApp() {
        tasks = new ArrayList<>();
        loadTasks();
        initializeUI();
        setupListeners();
    }

    private void initializeUI() {
        setTitle("Pink Todo App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PINK_BACKGROUND);

        listModel = new DefaultListModel<>();
        updateListModel();
        
        taskList = new JList<>(listModel);
        taskList.setBackground(Color.WHITE);
        taskList.setSelectionBackground(PINK_BUTTON);
        taskList.setSelectionForeground(Color.WHITE);
        taskList.setCellRenderer(new DefaultListCellRenderer() {
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
                        setFont(getFont().deriveFont(Font.BOLD));
                        setForeground(DARK_PINK); // Исправлено: теперь использует DARK_PINK
                    }
                    
                    if (isSelected) {
                        setBackground(PINK_BUTTON);
                        setForeground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });

        titleField = new JTextField(20);
        styleTextField(titleField);
        
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setForeground(DARK_PINK); // Исправлено
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        String[] defaultTags = {"", "🏠 Дом", "💼 Работа", "🎓 Учеба", "🛒 Покупки", "🏥 Здоровье", "✈️ Путешествие", "🎉 Развлечение"};
        tagComboBox = new JComboBox<>(defaultTags);
        styleComboBox(tagComboBox);

        datePicker = new DatePickerPanel();

        addButton = createPinkButton("Добавить");
        deleteButton = createPinkButton("Удалить");
        completeButton = createPinkButton("Выполнено");
        editButton = createPinkButton("Редактировать");

        JPanel inputPanel = createInputPanel();
        JPanel controlPanel = createControlPanel();

        JLabel titleLabelMain = new JLabel("Мои Задачи", JLabel.CENTER);
        titleLabelMain.setFont(titleLabelMain.getFont().deriveFont(Font.BOLD, 20));
        titleLabelMain.setForeground(DARK_PINK);
        titleLabelMain.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        titleLabelMain.setBackground(new Color(255, 228, 225));
        titleLabelMain.setOpaque(true);

        setLayout(new BorderLayout());
        add(titleLabelMain, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(PINK_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PINK_BACKGROUND);
        JLabel titleLabel = new JLabel("Заголовок:");
        titleLabel.setForeground(DARK_PINK);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        
        JPanel tagDeadlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tagDeadlinePanel.setBackground(PINK_BACKGROUND);
        JLabel tagLabel = new JLabel("Тег:");
        tagLabel.setForeground(DARK_PINK);
        tagLabel.setFont(tagLabel.getFont().deriveFont(Font.BOLD));
        tagDeadlinePanel.add(tagLabel);
        tagDeadlinePanel.add(tagComboBox);
        
        JLabel deadlineLabel = new JLabel("Дедлайн:");
        deadlineLabel.setForeground(DARK_PINK);
        deadlineLabel.setFont(deadlineLabel.getFont().deriveFont(Font.BOLD));
        tagDeadlinePanel.add(deadlineLabel);
        tagDeadlinePanel.add(datePicker);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(PINK_BACKGROUND);
        JLabel descLabel = new JLabel("Описание:");
        descLabel.setForeground(DARK_PINK);
        descLabel.setFont(descLabel.getFont().deriveFont(Font.BOLD));
        descPanel.add(descLabel, BorderLayout.NORTH);
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        JPanel buttonPanelTop = new JPanel(new FlowLayout());
        buttonPanelTop.setBackground(PINK_BACKGROUND);
        buttonPanelTop.add(addButton);
        
        inputPanel.add(titlePanel, BorderLayout.NORTH);
        inputPanel.add(tagDeadlinePanel, BorderLayout.CENTER);
        inputPanel.add(descPanel, BorderLayout.SOUTH);
        inputPanel.add(buttonPanelTop, BorderLayout.AFTER_LAST_LINE);

        return inputPanel;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(PINK_BACKGROUND);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.add(deleteButton);
        controlPanel.add(completeButton);
        controlPanel.add(editButton);
        return controlPanel;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(DARK_PINK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setCaretColor(PINK_BORDER);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(DARK_PINK); 
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PINK_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
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
        String tag = (String) tagComboBox.getSelectedItem();
        String deadline = datePicker.getDate();
        
        if (!title.isEmpty()) {
            Task task = new Task(tasks.size() + 1, title, description, tag, deadline);
            tasks.add(task);
            updateListModel();
            clearInputFields();
            titleField.requestFocus();
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "Введите заголовок задачи!", "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearInputFields() {
        titleField.setText("");
        descriptionArea.setText("");
        tagComboBox.setSelectedIndex(0);
        datePicker.setToToday();
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
            
            String[] tags = {"", "🏠 Дом", "💼 Работа", "🎓 Учеба", "🛒 Покупки", "🏥 Здоровье", "✈️ Путешествие", "🎉 Развлечение"};
            JComboBox<String> editTagComboBox = new JComboBox<>(tags);
            if (task.getTag() != null) {
                editTagComboBox.setSelectedItem(task.getTag());
            }
            
            DatePickerPanel editDatePicker = new DatePickerPanel();
            editDatePicker.setDate(task.getDeadline());
            
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            
            JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            fieldsPanel.add(new JLabel("Заголовок:"));
            fieldsPanel.add(editTitleField);
            fieldsPanel.add(new JLabel("Описание:"));
            fieldsPanel.add(new JScrollPane(editDescArea));
            fieldsPanel.add(new JLabel("Тег:"));
            fieldsPanel.add(editTagComboBox);
            fieldsPanel.add(new JLabel("Дедлайн:"));
            
            JPanel datePickerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            datePickerPanel.add(editDatePicker);
            fieldsPanel.add(datePickerPanel);
            
            panel.add(fieldsPanel, BorderLayout.CENTER);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Редактирование задачи", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                String newTitle = editTitleField.getText().trim();
                if (!newTitle.isEmpty()) {
                    task.setTitle(newTitle);
                    task.setDescription(editDescArea.getText().trim());
                    task.setTag((String) editTagComboBox.getSelectedItem());
                    task.setDeadline(editDatePicker.getDate());
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