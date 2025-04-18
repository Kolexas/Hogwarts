package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

@Service
@Transactional
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> findByColor(String color) {
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findByName(String name) {
        return facultyRepository.findByNameIgnoreCase(name);
    }

    public List<Faculty> findByNameAndColor(String name, String color) {
        return facultyRepository.findByColorIgnoreCaseAndNameIgnoreCase(name, color);
    }

    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new NoSuchElementException("Faculty with id " + facultyId + " not found"));
        return faculty.getStudents();
    }
}