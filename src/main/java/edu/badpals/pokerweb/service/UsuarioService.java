package edu.badpals.pokerweb.service;

import edu.badpals.pokerweb.dtos.RegistroUsuarioDTO;
import edu.badpals.pokerweb.dtos.LoginDTO;
import edu.badpals.pokerweb.dtos.UsuarioLogueadoDTO;
import edu.badpals.pokerweb.model.Usuario;
import edu.badpals.pokerweb.repository.UsuarioRepository;
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
            throw new RuntimeException("El email ya está registrado.");
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
    public LoginStatus login(LoginDTO logindto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(logindto.getEmail());

        if (usuarioOpt.isEmpty()) {
            return LoginStatus.USER_NOT_FOUND;
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(logindto.getPassword(), usuario.getPasswordHash())) {
            return LoginStatus.ERROR_PASSWORD;
        }

        if (!usuario.isActivo()) {
            return LoginStatus.USER_BLOCKED;
        }

        return LoginStatus.LOGIN_OK;
    }

    @Transactional
    public Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public UsuarioLogueadoDTO getUsuarioResponseByEmail(String email) {
        Usuario usuario = getUsuarioByEmail(email);
        return modelMapper.map(usuario, UsuarioLogueadoDTO.class);
    }
}
