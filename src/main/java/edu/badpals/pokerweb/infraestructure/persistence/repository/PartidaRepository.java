package edu.badpals.pokerweb.infraestructure.persistence.repository;

import edu.badpals.pokerweb.domain.model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, String> {
}

