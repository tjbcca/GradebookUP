-- Table for storing student information
CREATE TABLE Students (
    StudentID SERIAL PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50)
);

-- Table for storing course information
CREATE TABLE Courses (
    CourseID SERIAL PRIMARY KEY,
    CourseName VARCHAR(100),
    CourseDescription TEXT
);

-- Table for storing enrollment information (linking students to courses)
CREATE TABLE Enrollments (
    EnrollmentID SERIAL PRIMARY KEY,
    StudentID INT,
    CourseID INT,
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

-- Table for storing assignment information
CREATE TABLE Assignments (
    AssignmentID SERIAL PRIMARY KEY,
    CourseID INT,
    AssignmentName VARCHAR(100),
    FOREIGN KEY (CourseID) REFERENCES Courses(CourseID)
);

-- Table for storing assignment grades (linking students to assignments)
CREATE TABLE AssignmentGrades (
    AssignmentGradeID SERIAL PRIMARY KEY,
    StudentID INT,
    AssignmentID INT,
    Grade INT,
    FOREIGN KEY (StudentID) REFERENCES Students(StudentID),
    FOREIGN KEY (AssignmentID) REFERENCES Assignments(AssignmentID)
);

-- View to calculate the average grade for each student in each course
CREATE VIEW CourseGrades AS
SELECT
    e.StudentID,
    e.CourseID,
    AVG(ag.Grade) AS AverageGrade
FROM
    Enrollments e
        JOIN
    Assignments a ON e.CourseID = a.CourseID
        JOIN
    AssignmentGrades ag ON a.AssignmentID = ag.AssignmentID AND e.StudentID = ag.StudentID
GROUP BY
    e.StudentID, e.CourseID;