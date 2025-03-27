package edu.badpals.pokerweb.domain.repository;

import edu.badpals.pokerweb.domain.model.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String s);
}

