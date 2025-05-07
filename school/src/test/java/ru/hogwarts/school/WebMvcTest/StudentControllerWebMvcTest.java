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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.StudentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentRepository studentRepository;

    @SpyBean
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @Test
    public void postStudentTest() throws Exception {
        Student studentForPost = new Student();
        studentForPost.setName("Петя");
        studentForPost.setAge(15);
        studentForPost.setId(1);
        JSONObject studentObject = new JSONObject();
        studentObject.put("id", studentForPost.getId());
        studentObject.put("name", studentForPost.getName());
        studentObject.put("age", studentForPost.getAge());
        when(studentService.addStudent(any(Student.class))).thenReturn(studentForPost);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/students")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentInfoTest() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry Potter");
        student.setAge(11);
        when(studentService.findStudent(1L)).thenReturn(student);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(11));
    }

    @Test
    void getStudentsByAgeTest() throws Exception {
        List<Student> students = new ArrayList<>();
        Student student1 = new Student();
        student1.setId(1L);
        student1.setName("Harry");
        student1.setAge(11);
        Student student2 = new Student();
        student2.setId(2L);
        student2.setName("Ron");
        student2.setAge(12);
        Student student3 = new Student();
        student3.setId(3L);
        student3.setName("Hermione");
        student3.setAge(14);
        students.add(student1);
        students.add(student2);
        students.add(student3);
        when(studentService.findByAgeBetween(11, 12)).thenReturn(students);
        mockMvc.perform(MockMvcRequestBuilders.get("/students?min=11&max=12")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Harry"))
                .andExpect(jsonPath("$[0].age").value(11))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Ron"))
                .andExpect(jsonPath("$[1].age").value(12));
    }

    @Test
    void getFacultyByStudentIdTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("Red");
        Student student = new Student();
        student.setId(1);
        when(studentService.getFacultyByStudentId(student.getId())).thenReturn(faculty);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/" + 1 + "/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));
    }
    @Test
    public void getStudentParallel_shouldReturnOkAndCallServiceMethod() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/print-parallel")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getStudentParallelSync_shouldReturnOkAndCallServiceMethod() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/students/print-synchronized")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void editStudentTest() throws Exception {
        Student studentForEdit = new Student();
        studentForEdit.setId(1L);
        studentForEdit.setName("Harry Potter");
        studentForEdit.setAge(12);
        when(studentService.editStudent(any(Student.class))).thenReturn(studentForEdit);
        JSONObject studentObject = new JSONObject();
        studentObject.put("id", studentForEdit.getId());
        studentObject.put("name", studentForEdit.getName());
        studentObject.put("age", studentForEdit.getAge());
        mockMvc.perform(MockMvcRequestBuilders.put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(12));
    }

    @Test
    void deleteStudentTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(studentService, times(1)).deleteStudent(1L);
        mockMvc.perform(MockMvcRequestBuilders.get("/students/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}