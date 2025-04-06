package edu.badpals.pokerweb.application.service;

import edu.badpals.pokerweb.application.dtos.RegistroUsuarioDTO;
import edu.badpals.pokerweb.application.dtos.LoginDTO;
import edu.badpals.pokerweb.application.dtos.UsuarioLogueadoDTO;
import edu.badpals.pokerweb.domain.exceptions.PasswordIncorrectaException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioBloqueadoException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioNoEncontradoException;
import edu.badpals.pokerweb.domain.exceptions.UsuarioYaExisteException;
import edu.badpals.pokerweb.domain.model.Usuario;
import edu.badpals.pokerweb.infraestructure.persistence.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public enum LoginStatus {
        LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD, USER_BLOCKED
    }

    @Transactional
    public UsuarioLogueadoDTO registrar(RegistroUsuarioDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new UsuarioYaExisteException(dto.getEmail());
        }

        Usuario usuario = new Usuario(
                dto.getNombreCompleto(),
                dto.getDni(),
                dto.getFechaNacimiento(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword())
        );

        usuarioRepository.save(usuario);
        return modelMapper.map(usuario, UsuarioLogueadoDTO.class);
    }

    @Transactional
    public UsuarioLogueadoDTO login(LoginDTO logindto) {
        Usuario usuario = usuarioRepository.findByEmail(logindto.getEmail())
                .orElseThrow(() -> new UsuarioNoEncontradoException(logindto.getEmail()));

        if (!passwordEncoder.matches(logindto.getPassword(), usuario.getPasswordHash())) {
            throw new PasswordIncorrectaException();
        }

        if (!usuario.isActivo()) {
            throw new UsuarioBloqueadoException(usuario.getEmail());
        }

        return modelMapper.map(usuario, UsuarioLogueadoDTO.class);
    }


    @Transactional
    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }

    public UsuarioLogueadoDTO getUsuarioResponseByEmail(String email) {
        Usuario usuario = getUsuarioByEmail(email);
        return modelMapper.map(usuario, UsuarioLogueadoDTO.class);
    }
}