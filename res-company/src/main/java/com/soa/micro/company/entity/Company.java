package com.soa.micro.company.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Company {

	private String name;

	private List<User> users;
}
