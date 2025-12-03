package com.companyz.ems.dao;

import java.util.List;
import java.util.Optional;

import com.companyz.ems.model.Division;

public interface DivisionDao {
    Optional<Division> findById(int divisionId);
    List<Division> findAll();
    boolean createDivision(Division division);
    boolean updateDivision(Division division);
    boolean deleteDivision(int divisionId);
}
