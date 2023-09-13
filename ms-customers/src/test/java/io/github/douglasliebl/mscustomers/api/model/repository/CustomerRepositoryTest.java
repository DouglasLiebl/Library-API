package io.github.douglasliebl.mscustomers.api.model.repository;

import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CustomerRepository repository;

    @Test
    @DisplayName("Should return true when CPF already exists")
    public void returnTrueWhenCPFAlreadyExistsTest() {
        // given
        String cpf = "12312312324";
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF(cpf).build();
        entityManager.persist(customer);

        // when
        boolean exists = repository.existsByCPF(cpf);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when CPF does not exists")
    public void returnFalseWhenCPFDoesNotExistsTest() {
        // given
        String cpf = "12312312324";

        // when
        boolean exists = repository.existsByCPF(cpf);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when Email already exists")
    public void returnTrueWhenEmailAlreadyExistsTest() {
        // given
        String email = "james@gmail.com";
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email(email)
                .CPF("12312312324").build();
        entityManager.persist(customer);

        // when
        boolean exists = repository.existsByEmail(email);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when Email does not exists")
    public void returnFalseWhenEmailDoesNotExistsTest() {
        // given
        String email = "james@gmail.com";

        // when
        boolean exists = repository.existsByEmail(email);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save a customer")
    public void saveCustomerTest() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        // when
        Customer savedCustomer = repository.save(customer);

        // then
        assertThat(savedCustomer.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should get a customer by id")
    public void findByIdTest() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();
        entityManager.persist(customer);

        // when
        Optional<Customer> foundCustomer = repository.findById(customer.getId());

        // then
        assertThat(foundCustomer.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should delete a customer")
    public void deleteCustomerTest() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();
        entityManager.persist(customer);

        // when
        Customer foundCustomer = entityManager.find(Customer.class, customer.getId());
        repository.delete(foundCustomer);

        // then
        Customer deletedCustomer = entityManager.find(Customer.class, foundCustomer.getId());
        assertThat(deletedCustomer).isNull();
    }

}