package com.roaddefects.repository;

import com.roaddefects.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DefectRepository extends JpaRepository<Defect, Long> {

    @Query("select d from Defect d join fetch d.type order by d.id")
    List<Defect> findAllWithType();

    @Query("select d from Defect d join fetch d.type t where t.code = :code order by d.id")
    List<Defect> findByTypeWithType(@Param("code") String code);

    long countByType_Code(String code);
}
