package Maven.Project.user;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Entity, ID 타입>
public interface UserRepository extends JpaRepository<User, Long> {
}
