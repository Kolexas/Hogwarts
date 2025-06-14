package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping
    public ResponseEntity<List<Faculty>> findFaculties(
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String name) {
        if (color != null && !color.isBlank() && name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.findByNameAndColor(color, name));
        } else if (color != null && !color.isBlank()) {
            return ResponseEntity.ok(facultyService.findByColor(color));
        } else if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(facultyService.findByName(name));
        } else {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @GetMapping("/{facultyId}/students")
    public ResponseEntity<Collection<Student>> getStudentsByFacultyId(@PathVariable Long facultyId) {
        try {
            Collection<Student> students = facultyService.getStudentsByFacultyId(facultyId);
            return ResponseEntity.ok(students);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getLongest")
    public String getLongestName() {
       return facultyService.getLongName();
    }

    @GetMapping("/getNumber")
    public long getNumber() {
        return facultyService.getNumber();
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }
}
