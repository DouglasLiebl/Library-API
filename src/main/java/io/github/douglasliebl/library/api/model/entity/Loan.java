package io.github.douglasliebl.library.api.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String customer;

    @Column
    private String email;

    @JoinColumn(name = "book_id")
    @ManyToOne
    @JsonManagedReference
    private Book book;

    @Column
    private LocalDate loanDate;

    @Column
    private Boolean returned;
}
