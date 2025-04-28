package ru.hogwarts.school.service;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

@Service
@Transactional
public class FacultyService {

    private final FacultyRepository facultyRepository;
    Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(long id) {
        logger.info("Searching of faculty by id...");
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for edit faculty");
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for delete student");
        facultyRepository.deleteById(id);
    }

    public List<Faculty> findByColor(String color) {
        logger.info("Searching of faculties by color...");
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findByName(String name) {
        logger.info("Searching of faculties by name...");
        return facultyRepository.findByNameIgnoreCase(name);
    }

    public List<Faculty> findByNameAndColor(String name, String color) {
        logger.info("Searching of faculty by color and name...");
        return facultyRepository.findByColorIgnoreCaseAndNameIgnoreCase(name, color);
    }

    public Collection<Student> getStudentsByFacultyId(Long facultyId) {
        logger.info("Searching of students of the faculty...");
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> {
                    logger.error("Faculty with id {} not found", facultyId);
                    return new NoSuchElementException();
                });
        return faculty.getStudents();
    }
}