package co.com.crediya.r2dbc;

import co.com.crediya.model.user.LoginUser;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
> implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper,
                                         TransactionalOperator transactionalOperator,
                                         PasswordEncoder passwordEncoder) {

        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
        this.passwordEncoder = passwordEncoder;
    }

    private final TransactionalOperator transactionalOperator;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> saveUser(User user) {
        return super.save(user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                .build()).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(User user) {
        return repository.existsByEmail(user.getEmail());
    }

    @Override
    public Mono<Boolean> existsByDocumentId(User user) {
        return repository.existsByDocumentIdentification(user.getDocumentIdentification());
    }

    @Override
    public Mono<User> findByDocumentIdentification(String documentIdentification) {
        return repository.findByDocumentIdentification(documentIdentification)
                .map(this::toEntity);
    }

    @Override
    public Mono<User> login(LoginUser loginUser) {
        return repository.findByEmail(loginUser.getEmail())
                .filter(userEntity -> passwordEncoder.matches(loginUser.getPassword(), userEntity.getPassword()))
                .map(this::toEntity);
    }

    @Override
    public Flux<User> findAllByEmailIn(List<String> emails) {
        return repository.findAllByEmailIn(emails)
                .map(this::toEntity);
    }
}
