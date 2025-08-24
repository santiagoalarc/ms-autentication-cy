package co.com.crediya.jpa.user;

import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserJpaRepositoryAdapter extends AdapterOperations<User, UserEntity, String, UserJpaRepository>
implements UserRepository {

    protected UserJpaRepositoryAdapter(UserJpaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, userEntity -> mapper.map(userEntity, User.class));
    }

    @Override
    public Mono<User> saveUser(User user) {

        return Mono.justOrEmpty(repository.save(toData(user)))
                .map(this::toEntity);
    }

    @Override
    public Mono<Boolean> existsByEmail(User user) {
        return Mono.justOrEmpty(repository.existsByEmail(user.getEmail()));
    }

    @Override
    public Mono<User> findByDocumentIdentification(String documentIdentification) {
        return Mono.justOrEmpty(repository.findByDocumentIdentification(documentIdentification))
                .map(this::toEntity);
    }
}
