package edu.badpals.pokerweb.controller;

import edu.badpals.pokerweb.dtos.CrearPartidaDTO;
import edu.badpals.pokerweb.model.Partida;
import edu.badpals.pokerweb.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    @PostMapping("/crear")
    public ResponseEntity<Partida> crearPartida(@RequestBody CrearPartidaDTO dto) {
        try {
            Partida partida = partidaService.crearPartida(dto.getIdUsuario());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
