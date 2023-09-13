package io.github.douglasliebl.mscustomers.api.model.repository;

import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByCPF(String cpf);

    boolean existsByEmail(String email);

}
