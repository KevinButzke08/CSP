package github.project.CSP.repository;

import github.project.CSP.inventory.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
