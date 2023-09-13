package io.github.douglasliebl.mscustomers.api.service;

import io.github.douglasliebl.mscustomers.api.model.entity.Customer;

import java.util.Optional;

public interface CustomerService {

    Customer save(Customer request);

    Optional<Customer> getById(Long id);

    void delete(Customer customer);
}
