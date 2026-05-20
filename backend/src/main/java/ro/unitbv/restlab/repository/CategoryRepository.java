package ro.unitbv.restlab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.unitbv.restlab.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}