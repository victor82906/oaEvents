package com.vmr.oaevents.repository;

import com.vmr.oaevents.model.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    boolean existByCif(String cif);

    boolean existByCifAndIdNot(String cif, Long id);

    Page<Empresa> findByActiva(boolean activa, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(e.cif) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Empresa> searchByNombreOrEmailOrCif(String termino, Pageable pageable);

}
