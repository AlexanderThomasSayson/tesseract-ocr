package com.srllc.tesseract_ocr.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRedemptionFormDTO {

    private long id;

    @NotBlank(message = "Retailer name is required")
    @Size(max = 100, message = "Retailer name must be at most 100 characters")
    private String retailerName;

    @NotBlank(message = "Barangay is required")
    @Size(max = 100, message = "Barangay must be at most 100 characters")
    private String baranggay;

    @NotBlank(message = "Municipality is required")
    @Size(max = 100, message = "Municipality must be at most 100 characters")
    private String municipality;

    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province must be at most 100 characters")
    private String province;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid contact number format")
    private String contactNumber;

    @NotBlank(message = "Farm area is required")
    @Size(max = 50, message = "Farm area must be at most 50 characters")
    private String farmArea;

    @NotBlank(message = "NK Hybrid Purchased is required")
    @Size(max = 50, message = "NK Hybrid Purchased must be at most 50 characters")
    private String nkHybridPurchased;

    @NotBlank(message = "Number of bags purchased is required")
    @Size(max = 20, message = "Bags purchased must be at most 20 characters")
    private String bagsPurchased;

    @NotBlank(message = "Receipt number is required")
    @Size(max = 50, message = "Receipt number must be at most 50 characters")
    private String receiptNumber;

    @NotBlank(message = "Voucher number is required")
    @Size(max = 50, message = "Voucher number must be at most 50 characters")
    private String voucherNumber;
}
