package tutorial.resthateoas;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name="CUSTOMER_ORDER")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue private Long id;
    private String description;
    private Status status;

    public Order(String description, Status status){
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof  Order))
            return false;
        Order order = (Order) o;
        return Objects.equals(this.id, order.id) &&
                Objects.equals(this.description, order.description) &&
                this.status == order.status;
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.description, this.status);
    }

    @Override
    public String toString(){
        return String.format("Order[id=%d, desc=%s, status=%s]",
                this.id, this.description, this.status);
    }
}
