package ru.hogwarts.school.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Searching of student by id...");
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for delete student");
        studentRepository.deleteById(id);
    }

    public List<Student> findByAge(int age) {
        logger.info("Searching of students by age...");
        return studentRepository.findByAge(age);
    }

    public List<Student> findByAgeBetween(int min, int max) {
        logger.info("Searching of students within age threshold...");
        return studentRepository.findByAgeBetween(min, max);
    }

    public Faculty getFacultyByStudentId(Long studentId) {
        Student student = studentRepository.findById(studentId).orElse(null);
        logger.info("Searching of faculty by student...");
        if (student == null) {
            return null;
        }
        return student.getFaculty();
    }

    public Integer getStudentsNumber() {
        logger.info("Getting number of students of the faculty...");
        return studentRepository.getStudentsNumber();
    }

    public Integer getStudentsAverageAge() {
        logger.info("Getting average age of students of the faculty...");
        return studentRepository.getStudentsAverageAge();
    }

    public List<Student> getBottomFiveStudents() {
        logger.info("Was invoked method for getting five bottom students");
        return studentRepository.GetBottomFiveStudents();
    }

    public  List<Student> getAllStudents() {
        logger.info("Was invoked method for getting all students");
        return studentRepository.findAll().stream()
                .sorted(Comparator.comparing(student -> student.getName().toUpperCase()))
                .collect(Collectors.toList());
    }

    public int getAverageAge() {
        logger.info("Getting average age of students");
        return (int) studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }
}

