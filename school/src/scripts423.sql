SELECT student.name,student.age,faculty.name
FROM student
left JOIN faculty ON student.faculty_id = faculty.id;

SELECT student.name
FROM student
INNER JOIN avatar ON student.id = avatar.student_id;