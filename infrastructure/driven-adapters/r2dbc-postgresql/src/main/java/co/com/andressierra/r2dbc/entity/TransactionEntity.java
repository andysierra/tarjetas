package co.com.andressierra.r2dbc.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("transactions")
public class TransactionEntity {
    @Id
    @Column
    private Long id;
    @Column
    private Long cardId;
    @Column
    private String reference;
    @Column
    private Integer validationNumber;
    @Column
    private BigDecimal totalAmount;
    @Column
    private String address;
    @Column
    private String status;
    @Column
    private LocalDateTime createdAt;
}