package tutorial.accessingdatajpa;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Customer {
    @Id @GeneratedValue
    private Long id;
    @NonNull private String firstName;
    @NonNull private String lastName;

    @Override
    public String toString(){
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}
