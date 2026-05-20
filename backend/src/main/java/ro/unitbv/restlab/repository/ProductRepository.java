package ro.unitbv.restlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.unitbv.restlab.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByPriceLessThan(Double price);
}
