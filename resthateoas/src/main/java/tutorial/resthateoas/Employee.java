package tutorial.resthateoas;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Employee {

    private @Id @GeneratedValue Long id;
    private @NonNull String firstName;
    private @NonNull String lastName;
    private @NonNull String role;

    public String getName(){
        return this.firstName + " " + this.lastName;
    }

    public void setName(String name){
        String[] parts = name.split(" ");
        this.firstName = parts[0];
        this.lastName = parts[1];
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (!(o instanceof Employee))
            return false;
        Employee employee = (Employee) o;
        return Objects.equals(this.id, employee.id) &&
                Objects.equals(this.firstName, employee.firstName) &&
                Objects.equals(this.lastName, employee.lastName) &&
                Objects.equals(this.role, employee.role);
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.id, this.firstName, this.lastName, this.role);
    }

    @Override
    public String toString(){
        return String.format("Employee[id=%d, firstname=%s, lastname=%s, role=%s]", id, firstName, lastName, role);
    }
}
