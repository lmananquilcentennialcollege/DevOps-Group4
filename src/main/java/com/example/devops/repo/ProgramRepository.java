package com.example.devops.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.devops.model.Program;

public interface ProgramRepository extends JpaRepository<Program, Long> {
	Optional<Program> findById(Long programCode); // This is usually provided by JpaRepository
}
