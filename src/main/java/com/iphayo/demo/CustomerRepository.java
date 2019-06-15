package com.iphayo.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {
  private Integer ID = 0;
  private List<Customer> customers;

  public CustomerRepository() {
    customers = new ArrayList<>();
  }

  public Customer save(Customer customer) {
    if(customer.getId() != null) {
      Optional<Customer> e = findById(customer.getId());
      if (e.isPresent()) {
        int idx = customers.indexOf(e.get());
        customers.add(idx, customer);
      }
    }
    customer.setId(ID++);
    customers.add(customer);
    return customer;
  }

  public List<Customer> findAll() {
    return customers;
  }

  public Optional<Customer> findById(Integer id) {
    return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
  }

  public void delete(Integer id) {
    Optional<Customer> customer = findById(id);
    customer.ifPresent(c -> customers.remove(c));
  }

}
