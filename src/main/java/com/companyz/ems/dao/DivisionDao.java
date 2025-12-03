package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Division;

public interface DivisionDao {
    Optional<Division> findById(int divisionId);
    Optional<Division> findByName(String divisionName);
    List<Division> findAll();
    Division createDivision(Division division);
    Division updateDivision(Division division);
    boolean deleteDivision(int divisionId);
}
