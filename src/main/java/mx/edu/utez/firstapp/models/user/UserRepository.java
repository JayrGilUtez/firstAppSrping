package mx.edu.utez.firstapp.models.user;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);
    /* Method queries
     * 1. Buscar a todos los usuarios activos
     * 2. Buscar a un usuario por medio del ID de la persona
     * 3. Buscar al usuario por medio del curp de la persona
     */
    //1
    List<User> findAllByStatus(Boolean status);
    //2
    Optional<User> findByPersonId(Long id);
    //3
    Optional<User> findByPersonCurp(String curp);
    @Query(value = "SELECT * FROM users u INNER JOIN people p ON u.person_id = p.id " +
            "WHERE p.birth_date BETWEEN :fechaUno AND :fechaDos", nativeQuery = true)
    List<User> obtenerUsuariosPorFechasDeNacimiento(@Param("fechaUno") String startDate,
                                                    @Param("fechaDos") String endDate);

    //2 puntos significa que va a buscar parametros nombrados (userId)
    @Modifying
    @Query(value = "INSERT INTO user_roles(user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    int saveUserRole(Long userId, Long roleId);

    @Query(value = "SELECT 1 FROM user_roles WHERE user_id = :userId AND " +
            "role_id = :roleId", nativeQuery = true)
    Long getIdUserRoles(Long userId, Long roleId);

}
