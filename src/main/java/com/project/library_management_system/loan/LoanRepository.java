package com.project.library_management_system.loan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
        List<Loan> findAllByReturned(boolean returned);
}
