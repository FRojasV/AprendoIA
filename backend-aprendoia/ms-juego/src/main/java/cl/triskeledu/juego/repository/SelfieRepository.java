package cl.triskeledu.juego.repository;

import cl.triskeledu.juego.model.Selfie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SelfieRepository extends JpaRepository<Selfie, Long> {

    List<Selfie> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    boolean existsByUsuarioIdAndPuntoId(Long usuarioId, Long puntoId);

    long countByUsuarioId(Long usuarioId);

    @Query("select coalesce(sum(s.puntos), 0) from Selfie s where s.usuarioId = :usuarioId")
    long sumarPuntos(Long usuarioId);

    @Query("select s.usuarioId as usuarioId, sum(s.puntos) as puntos from Selfie s group by s.usuarioId")
    List<PuntajeUsuario> puntosPorUsuario();

    void deleteByUsuarioId(Long usuarioId);
}
