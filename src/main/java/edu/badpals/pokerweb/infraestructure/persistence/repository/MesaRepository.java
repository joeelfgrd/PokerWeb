package edu.badpals.pokerweb.infraestructure.persistence.repository;

import edu.badpals.pokerweb.domain.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MesaRepository extends JpaRepository<Mesa, String> {
}
