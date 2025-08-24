package co.com.crediya.jpa.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserJpaRepository  extends CrudRepository<UserEntity, String>, QueryByExampleExecutor<UserEntity> {

    @Transactional
    boolean existsByEmail(String email);
    @Transactional
    Optional<UserEntity> findByDocumentIdentification(String documentIdentification);

}
