package com.cds.mini.repository;

import com.cds.mini.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserId(@Param("userId") String userId);

    List<User> findBySalaryBetween(BigDecimal lowerBound, BigDecimal upperBound);

    List<User> findBySalaryBetween(BigDecimal lowerBound, BigDecimal upperBound, Sort sort);
}
