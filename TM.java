import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TM extends JFrame {
    public TM() {
       
        setTitle("Task Manager & reminder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JButton Login_Button = new JButton("Login");

        Login_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (login(username, password)) {
                    openTaskManager(username);
                } else {
                    JOptionPane.showMessageDialog(TM.this, "Invalid username or password. Please try again.");
                }
            }
        });

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel());
        loginPanel.add(Login_Button);
        loginPanel.add(new JLabel());

        setLayout(new BorderLayout());
        add(loginPanel, BorderLayout.CENTER);
    }

    private boolean login(String username, String password) 
    {
        return username.equals("admin") && password.equals("admin");
    }

    private void openTaskManager(String username) 
    {
        TaskManagerFrame TM = new TaskManagerFrame(username);
        TM.setVisible(true);
        dispose();
    }

    private class TaskManagerFrame extends JFrame {
        private ArrayList<Task> tasks;
        private JTable taskTable;
        private DefaultTableModel tableModel;
        private String username;

        //private Timer reminderTimer;

        public TaskManagerFrame(String username) {
            this.username = username;

            setTitle("Task Manager & reminder");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);

            tasks = new ArrayList<>();

            tableModel = new DefaultTableModel(new Object[]{"Title", "Description", "Priority", "Due Date", "Time"}, 0);
            taskTable = new JTable(tableModel);
            
        JButton addButton = new JButton("Add");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");


        addButton.setIcon(resizeIcon(new ImageIcon("add_icon.png"), 20, 20));
        editButton.setIcon(resizeIcon(new ImageIcon("edit_icon.png"), 20, 20));
        deleteButton.setIcon(resizeIcon(new ImageIcon("delete_icon.png"), 20, 20));

    
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addTask();
                }
            });

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editTask();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteTask();
                }
            });

            JLabel currentDateLabel = new JLabel(getCurrentDate());
            currentDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(currentDateLabel, BorderLayout.NORTH);

            JLabel currentTimeLabel = new JLabel(getCurrentTime());
            currentTimeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            add(currentTimeLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);

            JScrollPane scrollPane = new JScrollPane(taskTable);

            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            loadTasksFromFile();
        }

        private String getCurrentDate() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();
            return dateFormat.format(currentDate);
        }

        private String getCurrentTime() {
            DateFormat timeFormat = new SimpleDateFormat("HH:MM");
            Date currentTime = new Date();
            return timeFormat.format(currentTime);
        }

        private void loadTasksFromFile() {
            try (BufferedReader reader = new BufferedReader(new FileReader(username + ".txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] taskData = line.split(",");
                    String title = taskData[0];
                    String description = taskData[1];
                    String priority = taskData[2];
                    String dueDate = taskData[3];
                    String reminderTime = taskData[4];
                    tasks.add(new Task(title, description, priority, dueDate, reminderTime));
                }
            } catch (IOException e) {
        }

            tasks.sort(new Comparator<Task>() {
    @Override
    public int compare(Task task1, Task task2) {
        
        int priority1 = getPriorityValue(task1.priority);
        int priority2 = getPriorityValue(task2.priority);
        return Integer.compare(priority1, priority2);
    }

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "High":
                return 0;
            case "Medium":
                return 1;
            case "Low":
                return 2;
            default:
                return 3;
        }
    }
});


            for (Task task : tasks) {
                tableModel.addRow(new Object[]{task.title, task.description, task.priority, task.dueDate});
            }
        }

        private void addTask() {
        String title = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task title:");
        String description = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task description:");

        String[] priorityOptions = {"High", "Medium", "Low"};
        String priority = (String) JOptionPane.showInputDialog(
                TaskManagerFrame.this,
                "Select the task priority:",
                "Priority",
                JOptionPane.PLAIN_MESSAGE,
                null,
                priorityOptions,
                "Medium"
        );

        String dueDate; 
        while (true) {
            dueDate = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task due date (yyyy-mm-dd):");
            if (isValidDateFormat(dueDate)) 
            {
                break;
            } else {
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Invalid date format. Please enter the date in yyyy-mm-dd format.");
            }
    }

    String reminderTime; // Add reminder time input
            while (true) {
                dueDate = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task due date (yyyy-mm-dd):");
                if (isValidDateFormat(dueDate)) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(TaskManagerFrame.this, "Invalid date format. Please enter the date in yyyy-mm-dd format.");
                }
            }

    reminderTime = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the task reminder time (HH:mm):");
    
    Task task = new Task(title, description, priority, dueDate, reminderTime);
    tasks.add(task);
    tableModel.addRow(new Object[]{task.title, task.description, task.priority, task.dueDate, task.reminderTime});

    saveTasksToFile();
}
        private boolean isValidDateFormat(String date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false);
    try {
        dateFormat.parse(date);
        return true;
    } catch (ParseException e) {
        return false;
    }
}

        private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
        Task task = tasks.get(selectedRow);

        String title = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task title:", task.title);
        String description = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task description:", task.description);

        String[] priorityOptions = {"High", "Medium", "Low"};
        String priority = (String) JOptionPane.showInputDialog(
                TaskManagerFrame.this,
                "Select the new task priority:",
                "Priority",
                JOptionPane.PLAIN_MESSAGE,
                null,
                priorityOptions,
                task.priority
        );

        String dueDate;
        while (true) {
            dueDate = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task due date (yyyy-mm-dd):");
            if (isValidDateFormat(dueDate)) {
                break;
            } else {
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Invalid date format. Please enter the date in yyyy-mm-dd format.");
            }
        }

        String reminderTime;
        
        reminderTime = JOptionPane.showInputDialog(TaskManagerFrame.this, "Enter the new task reminder time (HH:mm):");
        task.title = title;
        task.description = description;
        task.priority = priority;
        task.dueDate = dueDate;
        task.reminderTime = reminderTime;

        tableModel.setValueAt(task.title, selectedRow, 0);
        tableModel.setValueAt(task.description, selectedRow, 1);
        tableModel.setValueAt(task.priority, selectedRow, 2);
        tableModel.setValueAt(task.dueDate, selectedRow, 3);
        tableModel.setValueAt(task.reminderTime, selectedRow, 4); // Update reminder time in table

        saveTasksToFile();
    }
    }
       
        private void deleteTask() {
            int selectedRow = taskTable.getSelectedRow();
            if (selectedRow != -1) {
                tasks.remove(selectedRow);
                tableModel.removeRow(selectedRow);

                saveTasksToFile();
        }
    }
        private void saveTasksToFile() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(username + ".txt"))) {
                for (Task task : tasks) {
                    writer.write(task.title + "," + task.description + "," + task.priority + "," + task.dueDate + "," + task.reminderTime);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(TaskManagerFrame.this, "Error occurred while saving tasks to file.");
            }
        }

        private class Task {
        private String title;
        private String description;
        private String priority;
        private String dueDate;
        private String reminderTime;

        public Task(String title, String description, String priority, String dueDate, String reminderTime) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.dueDate = dueDate;
            this.reminderTime = reminderTime;
        }
    }
        private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }
    }

    
    @SuppressWarnings("unused")
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        pack();
    }

    public static void main(String args[]) 
    {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Shiva".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() 
            {
                new TM().setVisible(true);
            }
        });
    }
}
