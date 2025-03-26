package edu.badpals.pokerweb.controller;

import edu.badpals.pokerweb.dtos.CrearPartidaDTO;
import edu.badpals.pokerweb.model.Carta;
import edu.badpals.pokerweb.model.Partida;
import edu.badpals.pokerweb.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{id}/flop")
    public ResponseEntity<List<Carta>> repartirFlop(@PathVariable String id) {
        try {
            List<Carta> flop = partidaService.repartirFlop(id);
            return ResponseEntity.ok(flop);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/turn")
    public ResponseEntity<Carta> repartirTurn(@PathVariable String idPartida) {
        try {
            Carta turn = partidaService.repartirTurn(idPartida);
            return ResponseEntity.ok(turn);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/river")
    public ResponseEntity<Carta> repartirRiver(@PathVariable String idPartida) {
        try {
            Carta river = partidaService.repartirRiver(idPartida);
            return ResponseEntity.ok(river);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
