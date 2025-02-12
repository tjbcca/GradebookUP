import org.checkerframework.checker.units.qual.C;

import java.sql.*;
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
                System.out.println("2. View Courses");
                System.out.println("3. View Course details");
                System.out.println("4. Add Assignment");
                System.out.println("5. Add Course");
                System.out.println("6. Enroll Student");
                System.out.println("7. Add Student");
                System.out.println("8. Input Student Grade");
                System.out.println("9. Exit");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewAllStudents(stmt);
                        break;
                    case 2:
                        viewAllCourses(stmt);
                        break;
                    case 3:
                        viewCourse(conn);
                        break;
                    case 4:
                        addAssignment(conn, stmt);
                        break;
                    case 5:
                        addCourse(conn);
                        break;
                    case 6:
                        enrollStudent(conn);
                        break;
                    case 7:
                        addStudent(conn, stmt);
                        break;
                    case 8:
                        inputGrade(conn);
                        break;
                    case 9:
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

    public static void viewAllStudents(Statement stmt) {
        String query = "SELECT s.StudentID, s.FirstName, s.LastName, c.CourseID, c.CourseName, AVG(ag.Grade) AS AverageGrade " +
                "FROM Students s " +
                "LEFT JOIN Enrollments e ON s.StudentID = e.StudentID " +
                "LEFT JOIN Courses c ON e.CourseID = c.CourseID " +
                "LEFT JOIN Assignments a ON c.CourseID = a.CourseID " +
                "LEFT JOIN AssignmentGrades ag ON a.AssignmentID = ag.AssignmentID AND s.StudentID = ag.StudentID " +
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
                Double averageGrade = rs.getDouble("AverageGrade");

                if (studentID != currentStudentID) {
                    if (currentStudentID != -1) {
                        System.out.println();
                    }
                    System.out.printf("%s %s - Student ID: %d%n", firstName, lastName, studentID);
                    currentStudentID = studentID;
                }
                if (courseID != 0) {
                    System.out.printf("    Course: %d - %s, Average Grade: %.2f%n", courseID, courseName, averageGrade);
                } else {
                    System.out.println("    Unenrolled");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void viewAllCourses(Statement stmt) {
        String query = "SELECT c.CourseID, c.CourseName, COUNT(e.StudentID) AS StudentCount " +
                "FROM Courses c " +
                "LEFT JOIN Enrollments e ON c.CourseID = e.CourseID " +
                "GROUP BY c.CourseID, c.CourseName " +
                "ORDER BY c.CourseID";

        try (ResultSet rs = stmt.executeQuery(query)) {
            System.out.println("Course List:");
            while (rs.next()) {
                int courseID = rs.getInt("CourseID");
                String courseName = rs.getString("CourseName");
                int studentCount = rs.getInt("StudentCount");

                System.out.printf("%s - Course ID: %d%nNumber of Students: %d%n%n", courseName, courseID, studentCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addCourse(Connection conn) {
        Scanner input = new Scanner(System.in);
        input.nextLine();
        System.out.print("Enter Course Name: ");
        String courseName = input.nextLine();
        input.nextLine();
        System.out.print("Enter Course Description");
        String courseDescription = input.nextLine();
        try{
            Statement statement = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Courses (CourseName, CourseDescription) VALUES (?,?)");
            pstmt.setString(1, courseName);
            pstmt.setString(2, courseDescription);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            System.exit(0);

        }

    }
    public static void addAssignment(Connection conn, Statement stmt) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Course Name: ");
        String courseName = input.nextLine();
        System.out.print("Enter New Assignment Name: ");
        String assignmentName = input.nextLine();
        String query = "Select CourseID from Course where CourseName = "+ courseName;
        try (ResultSet rs = stmt.executeQuery(query)) {
            Integer courseID = rs.getInt("CourseID");
            Statement statement = conn.createStatement();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Assignments (CourseID, CourseDescription) VALUES (?,?)");
            pstmt.setInt(1, courseID);
            pstmt.setString(2, assignmentName);
            pstmt.executeUpdate();


        }
        catch (SQLException e){
            e.printStackTrace();
            System.exit(0);

        }

    }

    public static void addStudent(Connection conn, Statement stmt) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Student first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter Student last name: ");
        String lastName = input.nextLine();

        try{
            PreparedStatement init_check = conn.prepareStatement("select * from Students where firstName = ? and lastName = ?");
            init_check.setString(1, firstName);
            init_check.setString(2, lastName);
            ResultSet init_rs = init_check.executeQuery();

            if (!init_rs.next()) {
                PreparedStatement statement = conn.prepareStatement("insert into Students (FirstName, LastName) values (?,?)");
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.executeUpdate();
                statement.close();
                System.out.println("New student has been added.");
            } else {
                System.out.println("Student already exists.");
            }
            init_check.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
    public static void viewCourse(Connection conn) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Course Name: ");
        String courseName = input.nextLine();

        // Query to get all assignments for the course
        String assignmentsQuery = "SELECT a.AssignmentID, a.AssignmentName " +
                "FROM Courses c " +
                "LEFT JOIN Assignments a ON c.CourseID = a.CourseID " +
                "WHERE c.CourseName = ? " +
                "ORDER BY a.AssignmentID";

        // Query to get course details, students, and assignments
        String query = "SELECT c.CourseID, c.CourseName, e.StudentID, s.FirstName, s.LastName, a.AssignmentName " +
                "FROM Courses c " +
                "LEFT JOIN Enrollments e ON c.CourseID = e.CourseID " +
                "LEFT JOIN Students s ON e.StudentID = s.StudentID " +
                "LEFT JOIN Assignments a ON c.CourseID = a.CourseID " +
                "WHERE c.CourseName = ? " +
                "ORDER BY s.StudentID, a.AssignmentName";

        try (PreparedStatement assignmentsStmt = conn.prepareStatement(assignmentsQuery);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            assignmentsStmt.setString(1, courseName);
            stmt.setString(1, courseName);

            ResultSet assignmentsRs = assignmentsStmt.executeQuery();
            System.out.println("Assignments for the course:");
            while (assignmentsRs.next()) {
                int assignmentID = assignmentsRs.getInt("AssignmentID");
                String assignmentName = assignmentsRs.getString("AssignmentName");
                System.out.printf("    Assignment ID: %d, %s%n", assignmentID, assignmentName);
            }
            System.out.println();

            // Execute the main query and print the results
            ResultSet rs = stmt.executeQuery();
            Integer currentCourseID = null;
            Integer currentStudentID = null;
            boolean assignmentsPrinted = false;

            while (rs.next()) {
                Integer courseID = rs.getInt("CourseID");
                String courseNameResult = rs.getString("CourseName");
                Integer studentID = rs.getInt("StudentID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String assignmentName = rs.getString("AssignmentName");

                if (currentCourseID == null || !currentCourseID.equals(courseID)) {
                    if (currentCourseID != null) {
                        System.out.println();
                    }
                    System.out.printf("Course: %s (ID: %d)%n", courseNameResult, courseID);
                    currentCourseID = courseID;
                    currentStudentID = null;
                }

                if (currentStudentID == null || !currentStudentID.equals(studentID)) {
                    if (currentStudentID != null) {
                        System.out.println();
                    }
                    System.out.printf("    Student: %s %s (ID: %d)%n", firstName, lastName, studentID);
                    currentStudentID = studentID;
                    assignmentsPrinted = false;
                }

                if (assignmentName != null) {
                    if (!assignmentsPrinted) {
                        System.out.println("    Assignments:");
                        assignmentsPrinted = true;
                    }
                    System.out.printf("        %s%n", assignmentName);
                }
            }

            if (currentCourseID == null) {
                System.out.println("Course not found.");
            } else if (currentStudentID == null) {
                System.out.println("    No students enrolled.");
            } else if (!assignmentsPrinted) {
                System.out.println("    No assignments found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public static void enrollStudent(Connection conn) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Student ID: ");
        int studentID = input.nextInt();
        input.nextLine();  // Consume newline

        System.out.print("Enter Course ID: ");
        int courseID = input.nextInt();
        input.nextLine();  // Consume newline

        String insertQuery = "INSERT INTO Enrollments (StudentID, CourseID) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setInt(1, studentID);
            pstmt.setInt(2, courseID);
            pstmt.executeUpdate();
            System.out.println("Student enrolled successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void inputGrade(Connection conn) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter Course ID: ");
        int courseID = input.nextInt();
        input.nextLine();  // Consume newline

        System.out.print("Enter Student ID: ");
        int studentID = input.nextInt();
        input.nextLine();  // Consume newline

        System.out.print("Enter Assignment ID: ");
        int assignmentID = input.nextInt();
        input.nextLine();  // Consume newline

        System.out.print("Enter Grade: ");
        int grade = input.nextInt();
        input.nextLine();  // Consume newline

        try {
            // Check if the student is enrolled in the course
            String checkEnrollmentQuery = "SELECT COUNT(*) AS count FROM Enrollments WHERE StudentID = ? AND CourseID = ?";
            PreparedStatement checkEnrollmentStmt = conn.prepareStatement(checkEnrollmentQuery);
            checkEnrollmentStmt.setInt(1, studentID);
            checkEnrollmentStmt.setInt(2, courseID);
            ResultSet enrollmentRs = checkEnrollmentStmt.executeQuery();
            enrollmentRs.next();
            if (enrollmentRs.getInt("count") == 0) {
                System.out.println("Error: Student is not enrolled in the specified course.");
                return;
            }

            // Check if the assignment is part of the course
            String checkAssignmentQuery = "SELECT COUNT(*) AS count FROM Assignments WHERE AssignmentID = ? AND CourseID = ?";
            PreparedStatement checkAssignmentStmt = conn.prepareStatement(checkAssignmentQuery);
            checkAssignmentStmt.setInt(1, assignmentID);
            checkAssignmentStmt.setInt(2, courseID);
            ResultSet assignmentRs = checkAssignmentStmt.executeQuery();
            assignmentRs.next();
            if (assignmentRs.getInt("count") == 0) {
                System.out.println("Error: Assignment is not part of the specified course.");
                return;
            }

            // Insert the grade
            String insertGradeQuery = "INSERT INTO AssignmentGrades (StudentID, AssignmentID, Grade) VALUES (?, ?, ?)";
            PreparedStatement insertGradeStmt = conn.prepareStatement(insertGradeQuery);
            insertGradeStmt.setInt(1, studentID);
            insertGradeStmt.setInt(2, assignmentID);
            insertGradeStmt.setInt(3, grade);
            insertGradeStmt.executeUpdate();

            System.out.println("Grade added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
