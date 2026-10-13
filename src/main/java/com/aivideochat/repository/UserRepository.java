package com.aivideochat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aivideochat.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}