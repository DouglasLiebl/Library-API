package io.github.douglasliebl.mscustomers.api.service.impl;

import io.github.douglasliebl.mscustomers.api.exception.BusinessException;
import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import io.github.douglasliebl.mscustomers.api.model.repository.CustomerRepository;
import io.github.douglasliebl.mscustomers.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public Customer save(Customer request) {
        if (repository.existsByCPF(request.getCPF())) throw new BusinessException("CPF already registered.");
        if (repository.existsByEmail(request.getEmail())) throw new BusinessException("Email already registered.");

        return repository.save(request);
    }

    @Override
    public Optional<Customer> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Customer customer) {
        if (customer == null || customer.getId() == null) throw new IllegalArgumentException("Customer cannot be empty.");
        repository.delete(customer);
    }
}
