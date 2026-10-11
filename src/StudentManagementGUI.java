
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentManagementGUI extends JFrame {

    private final StudentManager manager = new StudentManager();

    private JTextField idField;
    private JTextField nameField;
    private JTextField courseField;
    private JTextField marksField;

    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentManagementGUI() {
        setTitle("Student Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout(10, 10));

        JLabel heading = new JLabel(
                "STUDENT MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        add(heading, BorderLayout.NORTH);

        // Student input form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        idField = new JTextField();
        nameField = new JTextField();
        courseField = new JTextField();
        marksField = new JTextField();

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Course:"));
        formPanel.add(courseField);

        formPanel.add(new JLabel("Marks (0-100):"));
        formPanel.add(marksField);

        // Buttons
        JButton addButton = new JButton("Add Student");
        JButton searchButton = new JButton("Search");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // Student table
        String[] columns = {
                "ID", "Name", "Course", "Marks", "Grade"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        studentTable.setRowHeight(25);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );
        studentTable.setSelectionBackground(new Color(184, 207, 229));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(
                BorderFactory.createTitledBorder("Student Records")
        );
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> addStudent());
        searchButton.addActionListener(e -> searchStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearFields());

        // Fill form when a table row is selected
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && studentTable.getSelectedRow() != -1) {

                int row = studentTable.getSelectedRow();

                idField.setText(
                        tableModel.getValueAt(row, 0).toString()
                );
                nameField.setText(
                        tableModel.getValueAt(row, 1).toString()
                );
                courseField.setText(
                        tableModel.getValueAt(row, 2).toString()
                );
                marksField.setText(
                        tableModel.getValueAt(row, 3).toString()
                );
            }
        });

        refreshTable();
    }

    private int readId() {
        String text = idField.getText().trim();

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Enter the student ID.");
        }

        int id = Integer.parseInt(text);

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Student ID must be a positive number."
            );
        }

        return id;
    }

    private double readMarks() {
        String text = marksField.getText().trim();

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Enter the marks.");
        }

        double marks = Double.parseDouble(text);

        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException(
                    "Marks must be between 0 and 100."
            );
        }

        return marks;
    }

    private void validateNameAndCourse() {
        if (nameField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Enter the student name."
            );
        }

        if (courseField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Enter the course."
            );
        }
    }

    private void addStudent() {
        try {
            int id = readId();
            validateNameAndCourse();
            double marks = readMarks();

            Student student = new Student(
                    id,
                    nameField.getText().trim(),
                    courseField.getText().trim(),
                    marks
            );

            if (manager.addStudent(student)) {
                manager.saveStudents();
                refreshTable();
                clearFields();

                JOptionPane.showMessageDialog(
                        this, "Student added successfully!"
                );

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "This student ID already exists.",
                        "Duplicate ID",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {
            showError("ID must be a whole number and marks must be numeric.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void searchStudent() {
        try {
            int id = readId();
            Student student = manager.searchStudent(id);

            if (student == null) {
                JOptionPane.showMessageDialog(
                        this, "Student not found."
                );
            } else {
                nameField.setText(student.getName());
                courseField.setText(student.getCourse());
                marksField.setText(
                        String.valueOf(student.getMarks())
                );

                studentTable.clearSelection();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (Integer.parseInt(
                            tableModel.getValueAt(i, 0).toString()
                    ) == id) {
                        studentTable.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }

        } catch (NumberFormatException ex) {
            showError("Enter a valid numeric student ID.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            int id = readId();
            validateNameAndCourse();
            double marks = readMarks();

            boolean updated = manager.updateStudent(
                    id,
                    nameField.getText().trim(),
                    courseField.getText().trim(),
                    marks
            );

            if (updated) {
                manager.saveStudents();
                refreshTable();
                clearFields();

                JOptionPane.showMessageDialog(
                        this, "Student updated successfully!"
                );
            } else {
                JOptionPane.showMessageDialog(
                        this, "Student ID not found."
                );
            }

        } catch (NumberFormatException ex) {
            showError("ID must be a whole number and marks must be numeric.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = readId();
            Student student = manager.searchStudent(id);

            if (student == null) {
                JOptionPane.showMessageDialog(
                        this, "Student not found."
                );
                return;
            }

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Delete student " + student.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                manager.deleteStudent(id);
                manager.saveStudents();
                refreshTable();
                clearFields();

                JOptionPane.showMessageDialog(
                        this, "Student deleted successfully!"
                );
            }

        } catch (NumberFormatException ex) {
            showError("Enter a valid numeric student ID.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Student student : manager.getAllStudents()) {
            Object[] row = {
                    student.getId(),
                    student.getName(),
                    student.getCourse(),
                    student.getMarks(),
                    student.getGrade()
            };

            tableModel.addRow(row);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        courseField.setText("");
        marksField.setText("");
        studentTable.clearSelection();
        idField.requestFocus();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}