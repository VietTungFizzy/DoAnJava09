package com.example.cypersoft.DoAnJava.controller;

import com.example.cypersoft.DoAnJava.dto.VoucherRequest;
import com.example.cypersoft.DoAnJava.dto.VoucherResponse;
import com.example.cypersoft.DoAnJava.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
public class VoucherController {
    
    private final VoucherService voucherService;
    
    @GetMapping
    public ResponseEntity<Page<VoucherResponse>> listVouchers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "storeId", required = false) Integer storeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(voucherService.listVouchers(keyword, status, storeId, page, size));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponse> getVoucher(@PathVariable Integer id) {
        return ResponseEntity.ok(voucherService.getById(id));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<VoucherResponse> getVoucherByCode(@PathVariable String code) {
        return ResponseEntity.ok(voucherService.getByCode(code));
    }
    
    @PostMapping
    public ResponseEntity<VoucherResponse> createVoucher(@RequestBody VoucherRequest request) {
        return ResponseEntity.ok(voucherService.create(request));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VoucherResponse> updateVoucher(@PathVariable Integer id, 
                                                       @RequestBody VoucherRequest request) {
        return ResponseEntity.ok(voucherService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Integer id) {
        voucherService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<VoucherResponse>> getActiveVouchers() {
        return ResponseEntity.ok(voucherService.getActiveVouchers());
    }
    
    @GetMapping("/active/store/{storeId}")
    public ResponseEntity<List<VoucherResponse>> getActiveVouchersByStore(@PathVariable Integer storeId) {
        return ResponseEntity.ok(voucherService.getActiveVouchersByStore(storeId));
    }
    
    @GetMapping("/applicable")
    public ResponseEntity<List<VoucherResponse>> getApplicableVouchers(
            @RequestParam(value = "storeId", required = false) Integer storeId,
            @RequestParam(value = "orderTotal", required = false) BigDecimal orderTotal
    ) {
        return ResponseEntity.ok(voucherService.getApplicableVouchers(storeId, orderTotal));
    }
    
    @PostMapping("/calculate-discount")
    public ResponseEntity<BigDecimal> calculateDiscount(
            @RequestParam String voucherCode,
            @RequestParam BigDecimal orderTotal,
            @RequestParam(value = "storeId", required = false) Integer storeId
    ) {
        BigDecimal discount = voucherService.calculateDiscount(voucherCode, orderTotal, storeId);
        return ResponseEntity.ok(discount);
    }
}
