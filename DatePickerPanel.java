import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerPanel extends JPanel {
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    
    public DatePickerPanel() {
        setLayout(new FlowLayout());
        
        // Дни
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }
        
        // Месяцы
        monthCombo = new JComboBox<>(new String[]{
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        });
        
        // Годы (текущий + 5 лет вперед)
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 5; i++) {
            yearCombo.addItem(i);
        }
        
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate date = LocalDate.parse(dateStr, formatter);
            dayCombo.setSelectedItem(date.getDayOfMonth());
            monthCombo.setSelectedIndex(date.getMonthValue() - 1);
            yearCombo.setSelectedItem(date.getYear());
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
    
    public boolean isEmpty() {
        return dayCombo.getSelectedItem() == null;
    }
}