package ru.hogwarts.school.WebMvcTest;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyRepository facultyRepository;

    @SpyBean
    private FacultyService facultyService;

    @InjectMocks
    private FacultyController facultyController;

    @Test
    void postFacultyTest() throws Exception {
        Faculty facultyForPost = new Faculty();
        facultyForPost.setName("Gryffindor");
        facultyForPost.setColor("Red");
        facultyForPost.setId(1L);
        when(facultyService.addFaculty(any(Faculty.class))).thenReturn(facultyForPost);
        JSONObject facultyJSON = new JSONObject();
        facultyJSON.put("id", facultyForPost.getId());
        facultyJSON.put("name", facultyForPost.getName());
        facultyJSON.put("color", facultyForPost.getColor());
        mockMvc.perform(MockMvcRequestBuilders.post("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(facultyJSON.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultyInfoTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        when(facultyService.findFaculty(1L)).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders.get("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void getFacultiesByColorTest() throws Exception {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Gryffindor");
        faculty1.setColor("Red");
        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName("Another");
        faculty2.setColor("Red");
        Faculty faculty3 = new Faculty();
        faculty3.setId(3L);
        faculty3.setName("Ravenclaw");
        faculty3.setColor("Blue");
        List<Faculty> faculties = List.of(faculty1, faculty2, faculty3);
        when(facultyService.findByColor("Red")).thenReturn(faculties.stream()
                .filter(faculty -> faculty.getColor().equals("Red"))
                .collect(Collectors.toList()));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculties?color=Red")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Another"))
                .andExpect(jsonPath("$[1].color").value("Red"))
                .andExpect(jsonPath("$[2]").doesNotExist());
    }

    @Test
    void getFacultiesByNameTest() throws Exception {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Gryffindor");
        faculty1.setColor("Red");
        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName("Gryffindor");
        faculty2.setColor("Blue");
        Faculty faculty3 = new Faculty();
        faculty3.setId(3L);
        faculty3.setName("Ravenclaw");
        faculty3.setColor("Yellow");
        List<Faculty> faculties = List.of(faculty1, faculty2, faculty3);
        when(facultyService.findByName("Gryffindor")).thenReturn(faculties.stream()
                .filter(faculty -> faculty.getName().equals("Gryffindor"))
                .collect(Collectors.toList()));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculties?name=Gryffindor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Gryffindor"))
                .andExpect(jsonPath("$[1].color").value("Blue"))
                .andExpect(jsonPath("$[2]").doesNotExist());
    }

    @Test
    void getStudentsByFacultyIdTest() throws Exception {
        long facultyId = 1L;
        Faculty faculty = new Faculty();
        faculty.setId(facultyId);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Harry");
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Ron");
        List<Student> students = List.of(student1, student2);
        faculty.setStudents(students);
        when(facultyRepository.findById(facultyId)).thenReturn(Optional.of(faculty));
        when(facultyService.getStudentsByFacultyId(facultyId)).thenReturn(students);
        mockMvc.perform(MockMvcRequestBuilders.get("/faculties/" + facultyId + "/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Harry"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Ron"));
    }

    @Test
    void editFacultyTest() throws Exception {
        Faculty facultyForEdit = new Faculty();
        facultyForEdit.setId(1L);
        facultyForEdit.setName("Gryffindor");
        facultyForEdit.setColor("Red");
        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(facultyForEdit);
        org.json.JSONObject facultyJson = new org.json.JSONObject();
        facultyJson.put("id", facultyForEdit.getId());
        facultyJson.put("name", facultyForEdit.getName());
        facultyJson.put("color", facultyForEdit.getColor());
        mockMvc.perform(MockMvcRequestBuilders.put("/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(facultyJson.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }

    @Test
    void deleteFacultyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(facultyService, times(1)).deleteFaculty(1L);
        mockMvc.perform(MockMvcRequestBuilders.get("/faculties/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
