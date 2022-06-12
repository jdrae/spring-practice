package tutorial.accessingdatajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired private CustomerRepository customerRepository;

    @PostMapping
    public Customer addNewCustomer(@RequestBody Customer customer){ // 실무에선 Dto 사용
        customerRepository.save(customer); // 실무에선 Service 에서 repository 접근
        return customer;
    }

    @GetMapping
    public Iterable<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }
}
