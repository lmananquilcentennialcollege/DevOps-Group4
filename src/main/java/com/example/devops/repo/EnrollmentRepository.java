package com.example.devops.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devops.model.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
