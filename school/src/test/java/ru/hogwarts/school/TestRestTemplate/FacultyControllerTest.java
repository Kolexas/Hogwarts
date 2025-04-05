package ru.hogwarts.school.TestRestTemplate;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @AfterEach
    void cleanup() {
        facultyRepository.deleteAllInBatch();
    }

    @Test
    void contextLoads() throws Exception {
        assertThat(facultyController).isNotNull();
    }

    @Test
    public void postFacultyTest() throws Exception {
        Faculty facultyForPost = new Faculty();
        facultyForPost.setName("Gryffindor");
        facultyForPost.setColor("Red");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", facultyForPost, Faculty.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);
    }

    @Test
    public void getFacultyInfoTest() throws Exception {
        Faculty facultyForGet = new Faculty();
        facultyForGet.setName("Ravenclaw");
        facultyForGet.setColor("Blue");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", facultyForGet, Faculty.class);
        Faculty createdfaculty = createResponse.getBody();
        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculties/" + createdfaculty.getId(), Faculty.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty retrievedFaculty = createResponse.getBody();
        assertNotNull(retrievedFaculty);
        assertThat(retrievedFaculty.getId()).isEqualTo(createdfaculty.getId());
        assertThat(retrievedFaculty.getName()).isEqualTo(createdfaculty.getName());
        assertThat(retrievedFaculty.getColor()).isEqualTo(createdfaculty.getColor());
    }

    @Test
    void getFacultiesByColorOrNameTest() {
        String color = "Red";
        String name = "Gryffindor";
        Faculty faculty_1 = new Faculty();
        faculty_1.setName("Gryffindor");
        faculty_1.setColor("Red");
        Faculty faculty_2 = new Faculty();
        faculty_2.setName("Stanford");
        faculty_2.setColor("Red");
        Faculty faculty_3 = new Faculty();
        faculty_3.setName("Gryffindor");
        faculty_3.setColor("Yellow");
        ResponseEntity<Faculty> createFacultyResponse_1 = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", faculty_1, Faculty.class);
        ResponseEntity<Faculty> createFacultyResponse_2 = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", faculty_2, Faculty.class);
        ResponseEntity<Faculty> createFacultyResponse_3 = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", faculty_3, Faculty.class);
        assertEquals(HttpStatus.OK, createFacultyResponse_1.getStatusCode());
        assertEquals(HttpStatus.OK, createFacultyResponse_2.getStatusCode());
        assertEquals(HttpStatus.OK, createFacultyResponse_3.getStatusCode());
        ResponseEntity<Faculty[]> responseForColor = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculties?color=" + color, Faculty[].class);
        assertEquals(HttpStatus.OK, responseForColor.getStatusCode());
        List<Faculty> facultiesWithColor = Arrays.asList(Objects.requireNonNull(responseForColor.getBody()));
        assertThat(facultiesWithColor).hasSize(2);
        assertThat(facultiesWithColor).extracting(Faculty::getColor).containsOnly(color);
        assertEquals(2, facultiesWithColor.size());
        assertEquals(color, facultiesWithColor.get(0).getColor());
        assertEquals(color, facultiesWithColor.get(1).getColor());
        ResponseEntity<Faculty[]> responseForName = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculties?name=" + name, Faculty[].class);
        assertEquals(HttpStatus.OK, responseForName.getStatusCode());
        List<Faculty> facultiesWithName = Arrays.asList(Objects.requireNonNull(responseForName.getBody()));
        assertThat(facultiesWithName).hasSize(2);
        assertThat(facultiesWithName).extracting(Faculty::getName).containsOnly(name);
    }

    @Test
    void getStudentsByFacultyId() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("red");
        ResponseEntity<Faculty> createFacultyResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", faculty, Faculty.class);
        assertThat(createFacultyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty createdFaculty = createFacultyResponse.getBody();
        assertNotNull(createdFaculty);
        Student student = new Student();
        student.setName("Harry Potter");
        student.setAge(11);
        student.setFaculty(createdFaculty);
        ResponseEntity<Student> createStudentResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/students", student, Student.class);
        assertThat(createStudentResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculties/" + createdFaculty.getId() + "/students", Student[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Student> students = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("Harry Potter");
    }

    @Test
    void editFacultyTest() throws Exception {
        Faculty facultyForEdit = new Faculty();
        facultyForEdit.setName("Slytherin");
        facultyForEdit.setColor("Green");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", facultyForEdit, Faculty.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);
        createdFaculty.setName("Harvard");
        ResponseEntity<Faculty> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/faculties", HttpMethod.PUT, new HttpEntity<>(createdFaculty), Faculty.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty updatedFaculty = updateResponse.getBody();
        assertNotNull(updatedFaculty);
        assertThat(updatedFaculty.getId()).isEqualTo(createdFaculty.getId());
        assertThat(updatedFaculty.getName()).isEqualTo("Harvard");
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Faculty facultyForDelete = new Faculty();
        facultyForDelete.setName("Hufflepuff");
        facultyForDelete.setColor("Yellow");
        ResponseEntity<Faculty> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculties", facultyForDelete, Faculty.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty createdFaculty = createResponse.getBody();
        assertNotNull(createdFaculty);
        restTemplate.delete("http://localhost:" + port + "/faculties/" + createdFaculty.getId());
        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculties/" + createdFaculty.getId(), Faculty.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
