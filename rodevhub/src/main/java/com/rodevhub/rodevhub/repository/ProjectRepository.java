package com.rodevhub.rodevhub.repository;

import com.rodevhub.rodevhub.entity.Project;
import com.rodevhub.rodevhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByUserOrderByProductionDateDesc(User user);

    List<Project> findByUserIdOrderByProductionDateDesc(Long userId);

    void deleteByIdAndUser(Long id, User user);

    long countByUser(User user);
}
