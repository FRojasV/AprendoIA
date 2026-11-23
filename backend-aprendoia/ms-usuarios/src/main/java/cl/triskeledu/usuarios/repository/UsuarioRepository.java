package cl.triskeledu.usuarios.repository;

import cl.triskeledu.usuarios.model.Rol;
import cl.triskeledu.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Acceso a la tabla "usuarios". Spring Data genera las consultas a partir del nombre del método. */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findByRolOrderByNombreAsc(Rol rol);

    List<Usuario> findAllByOrderByRolAscNombreAsc();

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);
}
