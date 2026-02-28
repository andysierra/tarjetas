package co.com.andressierra.r2dbc.entity;

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
@Table("card")
public class CardEntity {
    @Id
    @Column
    private Long id;
    @Column
    private String pan;
    @Column
    private String cardholderName;
    @Column
    private String cardholderId;
    @Column
    private String cardType;
    @Column
    private String phoneNumber;
    @Column
    private Integer validationNumber;
    @Column
    private String status;
    @Column
    private LocalDateTime createdAt;
}
