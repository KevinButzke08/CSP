package csp.controller;

import csp.inventory.PortfolioSnapshot;
import csp.service.SnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/snapshot")
public class SnapshotController {
    private final SnapshotService snapshotService;

    @Autowired
    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @GetMapping
    public ResponseEntity<List<PortfolioSnapshot>> getAllSnapshots() {
        return ResponseEntity.ok(snapshotService.getAllSnapshots());
    }

    @PostMapping("/snap")
    public ResponseEntity<Void> takeNewSnapshot() {
        snapshotService.takeNewSnapshot();
        return ResponseEntity.ok().build();
    }
}
