package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
> implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {

        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<User> saveUser(User user) {
        return super.save(user).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(User user) {
        return repository.existsByEmail(user.getEmail());
    }

    @Override
    public Mono<User> findByDocumentIdentification(String documentIdentification) {
        return repository.findByDocumentIdentification(documentIdentification)
                .map(this::toEntity);
    }
}
