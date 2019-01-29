package com.soa.micro.service.inventory.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.soa.micro.service.inventory.entity.Company;

public interface CompanyRepo extends JpaRepository<Company, Long> {

	@Modifying
	@Query("update #{#entityName} set staffCount = ?3,version = version+1 where id = ?1 and version = ?2")
	@Transactional
	int updateStaffCount(long id, int version, int staffCount);

	@Modifying
	@Query("update #{#entityName} set staffCount = staffCount-?2 where id = ?1 and staffCount >= ?2")
	int subStaffCountByConn(long id, int subCount);
}
