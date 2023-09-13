package io.github.douglasliebl.mscustomers.api.service;

import io.github.douglasliebl.mscustomers.api.exception.BusinessException;
import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import io.github.douglasliebl.mscustomers.api.model.repository.CustomerRepository;
import io.github.douglasliebl.mscustomers.api.service.impl.CustomerServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class CustomerServiceTest {

    CustomerService service;

    @MockBean
    CustomerRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new CustomerServiceImpl(repository);
    }

    @Test
    @DisplayName("Should register a customer")
    public void registerCustomerTest() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        // when
        Mockito.when(repository.existsByCPF(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(false);
        Mockito.when(repository.save(customer))
                .thenReturn(
                        Customer.builder()
                                .id(1L)
                                .firstName("James")
                                .lastName("Bond")
                                .email("james@gmail.com")
                                .CPF("12312312324").build());

        // then
        Customer savedCustomer = repository.save(customer);

        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getFirstName()).isEqualTo("James");
        assertThat(savedCustomer.getLastName()).isEqualTo("Bond");
        assertThat(savedCustomer.getEmail()).isEqualTo("james@gmail.com");
        assertThat(savedCustomer.getCPF()).isEqualTo("12312312324");
    }


    @Test
    @DisplayName("Should throw an Exception when trying to register a already used CPF")
    public void shouldNotSaveCustomerWithDuplicatedCPF() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        // when
        Mockito.when(repository.existsByCPF(Mockito.anyString()))
                .thenReturn(true);

        // then
        Throwable exception = Assertions.catchThrowable(() -> service.save(customer));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("CPF already registered.");

        Mockito.verify(repository, Mockito.never()).save(customer);
    }

    @Test
    @DisplayName("Should throw an Exception when trying to register a already used Email")
    public void shouldNotSaveCustomerWithDuplicatedEmail() {
        // given
        Customer customer = Customer.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        // when
        Mockito.when(repository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);

        // then
        Throwable exception = Assertions.catchThrowable(() -> service.save(customer));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email already registered.");

        Mockito.verify(repository, Mockito.never()).save(customer);
    }

    @Test
    @DisplayName("Should get a customer by id")
    public void getByIdTest() {
        // given
        Long id = 1L;
        Customer customer = Customer.builder()
                .id(id)
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        // when
        Mockito.when(repository.findById(id))
                .thenReturn(Optional.of(customer));
        Optional<Customer> foundCustomer = service.getById(id);

        // then
        assertThat(foundCustomer.isPresent()).isTrue();
        assertThat(foundCustomer.get().getId()).isEqualTo(customer.getId());
        assertThat(foundCustomer.get().getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(foundCustomer.get().getLastName()).isEqualTo(customer.getLastName());
        assertThat(foundCustomer.get().getEmail()).isEqualTo(customer.getEmail());
        assertThat(foundCustomer.get().getCPF()).isEqualTo(customer.getCPF());
    }

    @Test
    @DisplayName("Should return empty when customer not found")
    public void customerNotFoundTest() {
        // given
        Long id = 1L;

        // when
        Optional<Customer> foundCustomer = service.getById(id);

        // then
        assertThat(foundCustomer).isEmpty();
    }

    @Test
    @DisplayName("Should delete a customer")
    public void deleteCustomerTest() {
        // given
        Customer customer = Customer.builder().id(1L).build();

        // when
        assertDoesNotThrow(() -> service.delete(customer));

        // then
        Mockito.verify(repository, Mockito.times(1)).delete(customer);
    }

    @Test
    @DisplayName("Should throw an exception when trying to delete a not registered customer")
    public void deleteNotRegisteredCustomer() {
        // given
        Customer customer = new Customer();

        // when
        assertThrows(IllegalArgumentException.class, () -> service.delete(customer));

        // then
        Mockito.verify(repository, Mockito.never()).delete(customer);
    }
}