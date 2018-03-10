package com.soa.micro.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soa.micro.user.entity.User;

public interface UserRepo extends JpaRepository<User, Long>{

}
