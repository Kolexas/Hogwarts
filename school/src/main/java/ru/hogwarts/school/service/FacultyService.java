package ru.hogwarts.school.service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

@Service
public class FacultyService {

    private final HashMap<Long, Faculty> faculties = new HashMap<>();
    private long count = 0;

    public Faculty addFaculty(Faculty faculty) {
        faculty.setId(++count);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty findFaculty(long id) {
        return faculties.get(id);
    }

    public Faculty editFaculty(Faculty faculty) {
        if (!faculties.containsKey(faculty.getId())) {
            return null;
        }
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty deleteFaculty(long id) {
        return faculties.remove(id);
    }

    public List<Faculty> findByColor(String color) {
        return faculties.values().stream()
                .filter(faculty -> Objects.equals(faculty.getColor(), color))
                .collect(Collectors.toList());
    }
}