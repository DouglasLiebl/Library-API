package io.github.douglasliebl.mscustomers.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.douglasliebl.mscustomers.api.dto.CustomerDTO;
import io.github.douglasliebl.mscustomers.api.model.entity.Customer;
import io.github.douglasliebl.mscustomers.api.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    static String CUSTOMER_API = "/api/customer";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    @Test
    @DisplayName("Should register a new customer")
    public void registerCustomerTest() throws Exception {
        // given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        Customer savedCustomer = Customer.builder()
                .id(1L)
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        BDDMockito.given(customerService.save(Mockito.any(Customer.class)))
                .willReturn(savedCustomer);
        String json = new ObjectMapper().writeValueAsString(customerDTO);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("firstName").value(customerDTO.getFirstName()))
                .andExpect(jsonPath("lastName").value(customerDTO.getLastName()))
                .andExpect(jsonPath("email").value(customerDTO.getEmail()))
                .andExpect(jsonPath("cpf").value(customerDTO.getCPF()));
    }

    @Test
    @DisplayName("Should throw a Exception")
    public void registerInvalidCustomerTest() throws Exception {
        // given
        String json = new ObjectMapper().writeValueAsString(new Customer());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(CUSTOMER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(4)));
    }

    @Test
    @DisplayName("Should get a customer details")
    public void getCustomerTest() throws Exception {
        // given
        Long id = 1L;
        Customer customer = Customer.builder()
                .id(id)
                .firstName("James")
                .lastName("Bond")
                .email("james@gmail.com")
                .CPF("12312312324").build();

        BDDMockito.given(customerService.getById(id))
                .willReturn(Optional.of(customer));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("firstName").value("James"))
                .andExpect(jsonPath("lastName").value("Bond"))
                .andExpect(jsonPath("email").value("james@gmail.com"))
                .andExpect(jsonPath("cpf").value("12312312324"));
    }

    @Test
    @DisplayName("Should throw a Resource Not Found exception when customer does not exists")
    public void customerNotFoundTest() throws Exception {
        // given
        BDDMockito.given(customerService.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(CUSTOMER_API.concat("/" + 1L));

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete a customer")
    public void deleteCustomerTest() throws Exception {
        // given
        BDDMockito.given(customerService.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Customer.builder().id(1L).build()));

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(CUSTOMER_API.concat("/" + 1L))
                .accept(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }


}