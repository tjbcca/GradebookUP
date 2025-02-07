import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:postgresql:gradebook";
    private static final String USER = "postgres";
    private static final String PASSWORD = "6732";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                System.out.println("1. View All Students");
                System.out.println("2. View Student Courses");
                System.out.println("3. Add Assignment");
                System.out.println("4. Add Course");
                System.out.println("5. Enroll Student");
                System.out.println("6. Add Student");
                System.out.println("7. Input Student Grade");
                System.out.println("8. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1:
                        viewStudentsWithCoursesAndAverages(stmt);
                        break;
                    // Add other cases here
                    case 8:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewStudentsWithCoursesAndAverages(Statement stmt) {
        String query = "SELECT s.StudentID, s.FirstName, s.LastName, c.CourseID, c.CourseName, AVG(ag.Grade) AS AverageGrade " +
                "FROM Enrollments e " +
                "JOIN Students s ON e.StudentID = s.StudentID " +
                "JOIN Courses c ON e.CourseID = c.CourseID " +
                "JOIN Assignments a ON e.CourseID = a.CourseID " +
                "JOIN AssignmentGrades ag ON a.AssignmentID = ag.AssignmentID AND e.StudentID = ag.StudentID " +
                "GROUP BY s.StudentID, s.FirstName, s.LastName, c.CourseID, c.CourseName " +
                "ORDER BY s.StudentID, c.CourseID";

        try (ResultSet rs = stmt.executeQuery(query)) {
            int currentStudentID = -1;
            while (rs.next()) {
                int studentID = rs.getInt("StudentID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                int courseID = rs.getInt("CourseID");
                String courseName = rs.getString("CourseName");
                double averageGrade = rs.getDouble("AverageGrade");

                if (studentID != currentStudentID) {
                    if (currentStudentID != -1) {
                        System.out.println();
                    }
                    System.out.printf("%s %s - Student ID: %d%n", firstName, lastName, studentID);
                    currentStudentID = studentID;
                }
                System.out.printf("    Course: %d - %s, Average Grade: %.2f%n", courseID, courseName, averageGrade);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}