package ru.hogwarts.school.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<List<Student>> findStudents(@RequestParam(required = false) Integer age, @RequestParam(required = false) Integer min, @RequestParam(required = false) Integer max) {
        if (age != null) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        if (max != null && min != null) {
            return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/{studentId}/faculty")
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable Long studentId) {
        Faculty faculty = studentService.getFacultyByStudentId(studentId);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/total")
    public Integer getStudentsNumber() {
        return studentService.getStudentsNumber();
    }

    @GetMapping("/avgAge")
    public Integer getStudentsAverageAge() {
        return studentService.getStudentsAverageAge();
    }

    @GetMapping("/BottomFive")
    public List<Student> getBottomFiveStudents() {
        return studentService.getBottomFiveStudents();
    }
    @GetMapping("/getAll")
    public List<Student> getAll() {
        return studentService.getAllStudents();
    }
    @GetMapping("/avgAgeAll")
    public int getAverageAgeAll() {
        return studentService.getAverageAge();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}