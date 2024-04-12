package com.project.shopping.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shopping.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserName(String userName);

}
