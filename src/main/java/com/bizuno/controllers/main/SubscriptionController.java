package com.bizuno.controllers.main;

import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.CreateSubscriptionRequestDTO;
import com.bizuno.dtos.main.UpdateSubscriptionRequestDTO;
import com.bizuno.services.main.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bizuno/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<?> createSubscription(@RequestBody @Valid CreateSubscriptionRequestDTO createSubscriptionRequestDTO) {
        CommonResponse response = subscriptionService.createSubscription(createSubscriptionRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{subscriptionId}")
    public ResponseEntity<?> updateSubscription(@PathVariable UUID subscriptionId,
                                                 @RequestBody @Valid UpdateSubscriptionRequestDTO updateSubscriptionRequestDTO) {
        CommonResponse response = subscriptionService.updateSubscription(subscriptionId, updateSubscriptionRequestDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllSubscriptions(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(required = false) String sortBy,
                                                  @RequestParam(required = false) String sortDirection) {
        CommonResponse response = subscriptionService.getAllSubscriptions(page, size, sortBy, sortDirection);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<?> getSubscription(@PathVariable UUID subscriptionId) {
        CommonResponse response = subscriptionService.getSubscription(subscriptionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{subscriptionId}")
    public ResponseEntity<?> deleteSubscription(@PathVariable UUID subscriptionId) {
        CommonResponse response = subscriptionService.deleteSubscription(subscriptionId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
