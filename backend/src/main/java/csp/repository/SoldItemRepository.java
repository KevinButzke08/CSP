package csp.repository;

import csp.inventory.SoldItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SoldItemRepository extends JpaRepository<SoldItem, Long> {
}
