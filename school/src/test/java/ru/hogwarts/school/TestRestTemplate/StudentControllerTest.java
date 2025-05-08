package ru.hogwarts.school.TestRestTemplate;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;
import java.util.List;
import java.util.Objects;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void cleanup() {
        clearDatabase();
    }
    public void clearDatabase() {
        entityManager.createNativeQuery("Delete From avatar").executeUpdate();
        entityManager.createNativeQuery("Delete From student").executeUpdate();
        entityManager.createNativeQuery("Delete From faculty").executeUpdate();
        entityManager.clear();
        Long studentCount = (Long) entityManager.createNativeQuery("SELECT COUNT(*) FROM student").getSingleResult();
        Long facultyCount = (Long) entityManager.createNativeQuery("SELECT COUNT(*) FROM faculty").getSingleResult();
        Long avatarCount = (Long) entityManager.createNativeQuery("SELECT COUNT(*) FROM avatar").getSingleResult();
        System.out.println("Student count after truncate: " + studentCount);
        System.out.println("Faculty count after truncate: " + facultyCount);
        System.out.println("Avatar count after truncate: " + avatarCount);

        if (studentCount > 0 || facultyCount > 0 || avatarCount > 0) {
            throw new IllegalStateException("Database is not empty after truncate!");
        }
    }
    @Test
    void contextLoads() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    public void postStudentTest() throws Exception {
        Student studentForPost = new Student();
        studentForPost.setName("Alice");
        studentForPost.setAge(14);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", studentForPost, Student.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student createdStudent = createResponse.getBody();
        assertNotNull(createdStudent);
    }
    @Test
    public void getStudentInfo() throws Exception {
        Student studentForGet = new Student();
        studentForGet.setName("Max");
        studentForGet.setAge(15);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", studentForGet, Student.class);
        Student createdStudent = createResponse.getBody();
        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/students/" + createdStudent.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student retrievedStudent = getResponse.getBody();
        assertNotNull(retrievedStudent);
        assertThat(retrievedStudent.getId()).isEqualTo(createdStudent.getId());
        assertThat(retrievedStudent.getName()).isEqualTo(createdStudent.getName());
        assertThat(retrievedStudent.getAge()).isEqualTo(createdStudent.getAge());
    }

    @Test
    void getStudentsByAgeTest() {
        Student student_1 = new Student();
        student_1.setName("Vlad");
        student_1.setAge(16);
        Student student_2 = new Student();
        student_2.setName("Maxim");
        student_2.setAge(20);
        Student student_3 = new Student();
        student_3.setName("Vladimir");
        student_3.setAge(20);
        restTemplate.postForEntity("http://localhost:" + port + "/students", student_1, Student.class);
        restTemplate.postForEntity("http://localhost:" + port + "/students", student_2, Student.class);
        restTemplate.postForEntity("http://localhost:" + port + "/students", student_3, Student.class);
        ResponseEntity<List<Student>> response = restTemplate.exchange(
                "http://localhost:" + port + "/students?age=20", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Student> students = response.getBody();
        assertNotNull(students);
        assertThat(students.size()).isEqualTo(2);
        assertThat(students.stream().allMatch(s -> s.getAge() == 20)).isTrue();
    }

    @Test
    void getFacultyByStudentIdTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setName("University of Pennsylvania");
        faculty.setColor("Lavender");
        ResponseEntity<Faculty> createFacultyResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", faculty, Faculty.class);
        assertThat(createFacultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        long facultyId = Objects.requireNonNull(createFacultyResponse.getBody()).getId();
        String facultyName = createFacultyResponse.getBody().getName();
        String facultyColor = createFacultyResponse.getBody().getColor();
        Student student123 = new Student();
        student123.setName("Harry Potter");
        faculty.setId(facultyId);
        faculty.setName(facultyName);
        faculty.setColor(facultyColor);
        student123.setFaculty(faculty);
        ResponseEntity<Student> createStudentResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", student123, Student.class);
        assertThat(createStudentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student createdStudent = createStudentResponse.getBody();
        assertNotNull(createdStudent);
        ResponseEntity<Faculty> getFacultyResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/students/" + createdStudent.getId() + "/faculty", Faculty.class);
        assertThat(getFacultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty retrievedFaculty = getFacultyResponse.getBody();
        assertNotNull(retrievedFaculty);
        assertThat(retrievedFaculty.getId()).isEqualTo(faculty.getId());
        assertThat(retrievedFaculty.getName()).isEqualTo(faculty.getName());
        assertThat(retrievedFaculty.getColor()).isEqualTo(faculty.getColor());
    }
    @Test
    public void getStudentParallel_Ok() {
        String url = "http://localhost:" + port + "/students/print-parallel";
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getStudentParallelSync_Ok() {
        String url = "http://localhost:" + port + "/students/print-synchronized";
        ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void editStudentTest() throws Exception {
        Student studentForEdit = new Student();
        studentForEdit.setName("Bob");
        studentForEdit.setAge(32);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", studentForEdit, Student.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student createdStudent = createResponse.getBody();
        assertNotNull(createdStudent);
        createdStudent.setName("Bob");
        ResponseEntity<Student> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/students", HttpMethod.PUT, new HttpEntity<>(createdStudent), Student.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student updatedStudent = updateResponse.getBody();
        assertNotNull(updatedStudent);
        assertThat(updatedStudent.getId()).isEqualTo(createdStudent.getId());
        assertThat(updatedStudent.getName()).isEqualTo("Bob");
    }

    @Test
    public void deleteUserTest() throws Exception {
        Student studentForDelete = new Student();
        studentForDelete.setName("Perry");
        studentForDelete.setAge(12);
        ResponseEntity<Student> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", studentForDelete, Student.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student createdStudent = createResponse.getBody();
        assertNotNull(createdStudent);
        restTemplate.delete("http://localhost:" + port + "/students/" + createdStudent.getId());
        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/students/" + createdStudent.getId(), Student.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
