package k6.gatherlove.wallet.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;

@Entity
@Table(
        name = "payment_methods",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id","payment_method_id"}
        )
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;                              // surrogate PK

    @Column(name = "user_id", nullable = false)
    private String userId;                        // user pemilik

    @Column(name = "payment_method_id", nullable = false, length = 64)
    private String paymentMethodId;               // kode pm-1, pm-2, etc.

    @Column(nullable = false)
    private String type;                          // CC, GoPay, dll.

    @Column(nullable = false)
    private boolean active = true;                // default aktif

    public PaymentMethod(String userId, String pmCode, String type) {
        this.userId = userId;
        this.paymentMethodId = pmCode;
        this.type = type;
        this.active = true;
    }
}