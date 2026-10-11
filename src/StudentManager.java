import java.util.ArrayList;

public class StudentManager {

    private ArrayList<Student> students = FileHandler.loadStudents();

    // Add a student
    public boolean addStudent(Student student) {
        if (searchStudent(student.getId()) != null) {
            return false;
        }

        students.add(student);
        return true;
    }

    // Display all students
    public void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No student records found.");
            return;
        }

        for (Student student : students) {
            System.out.println(
                    "ID: " + student.getId()
                            + ", Name: " + student.getName()
                            + ", Course: " + student.getCourse()
                            + ", Marks: " + student.getMarks()
                            + ", Grade: " + student.getGrade()
            );
        }
    }

    // Search a student by ID
    public Student searchStudent(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }

        return null;
    }

    // Delete a student by ID
    public boolean deleteStudent(int id) {
        Student student = searchStudent(id);

        if (student != null) {
            students.remove(student);
            return true;
        }

        return false;
    }

    // Update a student's details
    public boolean updateStudent(
            int id, String name, String course, double marks) {

        Student student = searchStudent(id);

        if (student != null) {
            student.setName(name);
            student.setCourse(course);
            student.setMarks(marks);
            return true;
        }

        return false;
    }

    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public void saveStudents() {
        FileHandler.saveStudents(students);
    }

}
