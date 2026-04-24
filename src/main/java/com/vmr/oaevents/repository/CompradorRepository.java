package com.vmr.oaevents.repository;

import com.vmr.oaevents.model.Comprador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {

    boolean existByDni(String dni);

    boolean existByDniAndIdNot(String dni, Long id);

    @Query("SELECT c FROM Comprador c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(c.dni) LIKE LOWER(CONCAT('%', :termino, '%'))")
    Page<Comprador> searchByNombreOrEmailOrDni(String termino, Pageable pageable);

}
