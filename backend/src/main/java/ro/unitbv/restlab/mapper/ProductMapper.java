package ro.unitbv.restlab.mapper;

import org.springframework.stereotype.Component;
import ro.unitbv.restlab.dto.ProductResponse;
import ro.unitbv.restlab.model.Product;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getCreatedAt()
        );
    }
}
