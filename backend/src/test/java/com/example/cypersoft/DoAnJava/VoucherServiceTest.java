package com.example.cypersoft.DoAnJava;

import com.example.cypersoft.DoAnJava.dto.VoucherRequest;
import com.example.cypersoft.DoAnJava.entity.Voucher;
import com.example.cypersoft.DoAnJava.repository.VoucherRepository;
import com.example.cypersoft.DoAnJava.service.VoucherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class VoucherServiceTest {
    
    @Autowired
    private VoucherService voucherService;
    
    @Autowired
    private VoucherRepository voucherRepository;
    
    @Test
    public void testCreateVoucher() {
        VoucherRequest request = new VoucherRequest();
        request.setCode("TEST10");
        request.setType(Voucher.VoucherType.PERCENT);
        request.setValue(BigDecimal.valueOf(10));
        request.setMaxDiscount(BigDecimal.valueOf(50000));
        request.setMinOrderTotal(BigDecimal.valueOf(100000));
        request.setStartDate(LocalDateTime.now());
        request.setEndDate(LocalDateTime.now().plusDays(30));
        request.setUsageLimit(100);
        request.setUsageLimitPerUser(1);
        request.setStatus(Voucher.VoucherStatus.ACTIVE);
        
        var response = voucherService.create(request);
        
        assertNotNull(response);
        assertEquals("TEST10", response.getCode());
        assertEquals(Voucher.VoucherType.PERCENT, response.getType());
        assertEquals(BigDecimal.valueOf(10), response.getValue());
        assertTrue(response.getIsActive());
    }
    
    @Test
    public void testCalculateDiscount() {
        // Create a test voucher
        VoucherRequest request = new VoucherRequest();
        request.setCode("DISCOUNT20");
        request.setType(Voucher.VoucherType.PERCENT);
        request.setValue(BigDecimal.valueOf(20));
        request.setMaxDiscount(BigDecimal.valueOf(100000));
        request.setMinOrderTotal(BigDecimal.valueOf(50000));
        request.setStatus(Voucher.VoucherStatus.ACTIVE);
        
        voucherService.create(request);
        
        // Test discount calculation
        BigDecimal orderTotal = BigDecimal.valueOf(200000);
        BigDecimal discount = voucherService.calculateDiscount("DISCOUNT20", orderTotal, null);
        
        // 20% of 200,000 = 40,000, but max discount is 100,000, so should be 40,000
        assertEquals(BigDecimal.valueOf(40000), discount);
    }
}
