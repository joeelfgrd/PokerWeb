package edu.badpals.pokerweb.interfaces.rest;

import edu.badpals.pokerweb.application.dtos.AccionJugadorDTO;
import edu.badpals.pokerweb.application.dtos.CrearPartidaDTO;
import edu.badpals.pokerweb.application.dtos.EstadoPartidaDTO;
import edu.badpals.pokerweb.application.dtos.ResultadoShowdownDTO;
import edu.badpals.pokerweb.domain.model.Carta;
import edu.badpals.pokerweb.domain.model.Partida;
import edu.badpals.pokerweb.application.service.PartidaService;
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
        return ResponseEntity.ok(partidaService.crearPartida(dto.getIdUsuario()));
    }

    @GetMapping("/{idPartida}/estado")
    public ResponseEntity<EstadoPartidaDTO> obtenerEstadoPartida(@PathVariable String idPartida) {
        return ResponseEntity.ok(partidaService.obtenerEstadoPartida(idPartida));
    }

    @PostMapping("/{idPartida}/showdown")
    public ResponseEntity<ResultadoShowdownDTO> resolverShowdown(@PathVariable String idPartida) {
        return ResponseEntity.ok(partidaService.resolverShowdown(idPartida));
    }

    @PostMapping("/{idPartida}/mano-privada")
    public ResponseEntity<Map<String, List<Carta>>> repartirManosPrivadas(@PathVariable String idPartida) {
        return ResponseEntity.ok(partidaService.repartirManosPrivadas(idPartida));
    }

    @PostMapping("/{idPartida}/nueva-mano")
    public ResponseEntity<Partida> nuevaMano(@PathVariable String idPartida) {
        return ResponseEntity.ok(partidaService.iniciarNuevaMano(idPartida));
    }

    @PostMapping("/{idPartida}/unirse")
    public ResponseEntity<Partida> unirseAPartida(@PathVariable String idPartida, @RequestParam String usuario) {
        return ResponseEntity.ok(partidaService.unirseAPartida(idPartida, usuario));
    }

    @PostMapping("/{idPartida}/apostar")
    public ResponseEntity<Partida> apostar(@PathVariable String idPartida, @RequestBody AccionJugadorDTO accion) {
        return ResponseEntity.ok(partidaService.apostar(idPartida, accion.getIdJugador(), accion.getCantidad()));
    }

    @PostMapping("/{idPartida}/igualar")
    public ResponseEntity<Partida> igualar(@PathVariable String idPartida, @RequestBody AccionJugadorDTO accion) {
        return ResponseEntity.ok(partidaService.igualar(idPartida, accion.getIdJugador()));
    }

    @PostMapping("/{idPartida}/pasar")
    public ResponseEntity<Partida> pasar(@PathVariable String idPartida, @RequestBody AccionJugadorDTO accion) {
        return ResponseEntity.ok(partidaService.pasar(idPartida, accion.getIdJugador()));
    }

    @PostMapping("/{idPartida}/retirarse")
    public ResponseEntity<Partida> retirarse(@PathVariable String idPartida, @RequestBody AccionJugadorDTO accion) {
        return ResponseEntity.ok(partidaService.retirarse(idPartida, accion.getIdJugador()));
    }

    @PostMapping("/{idPartida}/allin")
    public ResponseEntity<Partida> allIn(@PathVariable String idPartida, @RequestBody AccionJugadorDTO accion) {
        return ResponseEntity.ok(partidaService.allIn(idPartida, accion.getIdJugador()));
    }
}
