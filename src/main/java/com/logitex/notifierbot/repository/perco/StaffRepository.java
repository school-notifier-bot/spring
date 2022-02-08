package com.logitex.notifierbot.repository.perco;

import com.logitex.notifierbot.model.perco.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByFullFio(String fullFio);
    Optional<Staff> findByTabelID(Long tabelId);
}
