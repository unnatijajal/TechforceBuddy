package com.techforcebuddybl.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techforcebuddybl.entity.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {
	public FileEntity findByFileName(String fileName); 
}
