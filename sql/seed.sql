-- Seed data for Students table
INSERT INTO Students (FirstName, LastName) VALUES
    ('John', 'Doe'),
    ('Jane', 'Smith'),
    ('Alice', 'Johnson');

-- Seed data for Courses table
INSERT INTO Courses (CourseName, CourseDescription) VALUES
    ('Mathematics', 'An introduction to algebra, geometry, and calculus.'),
    ('History', 'A study of world history from ancient to modern times.'),
    ('Biology', 'An exploration of biological concepts and organisms.');

-- Seed data for Enrollments table
INSERT INTO Enrollments (StudentID, CourseID) VALUES
    (1, 1),
    (1, 2),
    (2, 1),
    (2, 3),
    (3, 2);

-- Seed data for Assignments table
INSERT INTO Assignments (CourseID, AssignmentName) VALUES
    (1, 'Algebra Homework'),
    (1, 'Geometry Quiz'),
    (2, 'Ancient Civilizations Essay'),
    (3, 'Biology Lab Report');

-- Seed data for AssignmentGrades table
INSERT INTO AssignmentGrades (StudentID, AssignmentID, Grade) VALUES
    (1, 1, 85),
    (1, 2, 90),
    (2, 1, 88),
    (3, 3, 75),
    (2, 4, 92);