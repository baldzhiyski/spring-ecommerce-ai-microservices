package org.baldzhiyski.product.repository;

import jakarta.persistence.LockModeType;
import org.baldzhiyski.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select p from Product p where p.id in :ids")
        List<Product> findAllForUpdateByIdIn(@Param("ids") Collection<Integer> ids);
}
