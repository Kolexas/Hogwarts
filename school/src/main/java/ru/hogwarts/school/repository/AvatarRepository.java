package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.AvatarsByPage;

import java.util.List;
import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByStudentId(Long studentId);

    @Query(value = "Select data FROM avatar", nativeQuery = true)
    List<AvatarsByPage> getAvatars();
}
