package edu.badpals.pokerweb.controller;

import edu.badpals.pokerweb.dtos.AccionJugadorDTO;
import edu.badpals.pokerweb.dtos.CrearPartidaDTO;
import edu.badpals.pokerweb.dtos.EstadoPartidaDTO;
import edu.badpals.pokerweb.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.model.Carta;
import edu.badpals.pokerweb.model.Partida;
import edu.badpals.pokerweb.service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{idPartida}/estado")
    public ResponseEntity<EstadoPartidaDTO> obtenerEstadoPartida(@PathVariable String idPartida) {
        try {
            EstadoPartidaDTO estado = partidaService.obtenerEstadoPartida(idPartida);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/{idPartida}/showdown")
    public ResponseEntity<ResultadoShowdownDTO> resolverShowdown(@PathVariable String idPartida) {
        try {
            ResultadoShowdownDTO resultado = partidaService.resolverShowdown(idPartida);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/{idPartida}/mano-privada")
    public ResponseEntity<Map<String, List<Carta>>> repartirManosPrivadas(@PathVariable String idPartida) {
        try {
            Map<String, List<Carta>> manos = partidaService.repartirManosPrivadas(idPartida);
            return ResponseEntity.ok(manos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/nueva-mano")
    public ResponseEntity<Partida> nuevaMano(@PathVariable String idPartida) {
        try {
            Partida nuevaMano = partidaService.iniciarNuevaMano(idPartida);
            return ResponseEntity.ok(nuevaMano);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{idPartida}/unirse")
    public ResponseEntity<Partida> unirseAPartida(@PathVariable String idPartida, @RequestParam String usuario) {
        try {
            Partida partida = partidaService.unirseAPartida(idPartida, usuario);
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/apostar")
    public ResponseEntity<Partida> apostar(
            @PathVariable String idPartida,
            @RequestBody AccionJugadorDTO accion) {
        try {
            Partida partida = partidaService.apostar(idPartida, accion.getIdJugador(), accion.getCantidad());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/igualar")
    public ResponseEntity<Partida> igualar(
            @PathVariable String idPartida,
            @RequestBody AccionJugadorDTO accion) {
        try {
            Partida partida = partidaService.igualar(idPartida, accion.getIdJugador());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/pasar")
    public ResponseEntity<Partida> pasar(
            @PathVariable String idPartida,
            @RequestBody AccionJugadorDTO accion) {
        try {
            Partida partida = partidaService.pasar(idPartida, accion.getIdJugador());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/retirarse")
    public ResponseEntity<Partida> retirarse(
            @PathVariable String idPartida,
            @RequestBody AccionJugadorDTO accion) {
        try {
            Partida partida = partidaService.retirarse(idPartida, accion.getIdJugador());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{idPartida}/allin")
    public ResponseEntity<Partida> allIn(
            @PathVariable String idPartida,
            @RequestBody AccionJugadorDTO accion) {
        try {
            Partida partida = partidaService.allIn(idPartida, accion.getIdJugador());
            return ResponseEntity.ok(partida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
