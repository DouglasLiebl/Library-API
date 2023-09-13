package io.github.douglasliebl.mscustomers.api.resource;

import io.github.douglasliebl.mscustomers.api.dto.CustomerDTO;
import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import io.github.douglasliebl.mscustomers.api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity registerCustomer(@RequestBody @Valid CustomerDTO request) {
        var customer = mapper.map(service.save(mapper.map(request, Customer.class)), CustomerDTO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("{id}")
    public ResponseEntity getCustomer(@PathVariable Long id) {
        var customer = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        service.delete(service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

}
