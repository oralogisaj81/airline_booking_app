package com.alnoor.backend.repository;

import com.alnoor.backend.model.Flight;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Flight f where f.id = :id")
    Optional<Flight> findByIdForUpdate(@Param("id") String id);

    @Query("select f from Flight f where f.originCode = :origin and f.destinationCode = :destination "
            + "and f.departureTime >= :dayStart and f.departureTime < :dayEnd order by f.departureTime asc")
    List<Flight> search(@Param("origin") String origin, @Param("destination") String destination,
                         @Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);
}
