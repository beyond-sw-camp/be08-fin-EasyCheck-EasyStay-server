package com.beyond.easycheck.reservationservices.infrastructure.repository;

import com.beyond.easycheck.reservationservices.infrastructure.entity.ReservationServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationServiceRepository extends JpaRepository<ReservationServiceEntity, Long> {

}
