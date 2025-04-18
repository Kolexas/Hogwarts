package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int min, int max);

    void deleteAllInBatch();

    @Query(value = "Select COUNT(*) FROM student", nativeQuery = true)
    Integer getStudentsNumber();

    @Query(value = "Select AVG(age) FROM student", nativeQuery = true)
    Integer getStudentsAverageAge();

    @Query(value = "Select * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<Student> GetBottomFiveStudents();
}
