package com.vmr.oaevents.repository;

import com.vmr.oaevents.model.Qr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrRepository extends JpaRepository<Qr, Long> {
    Optional<Qr> findByCodigo(String codigo);
}
