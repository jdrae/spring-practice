package tutorial.accessingdatajpa;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

// CrudRepository<엔티티타입, id타입>
// interface 만 작성하면 Spring Data JPA 가 앱 실행시 구현체를 만들어준다.
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);

    Customer findById(long id);

}
