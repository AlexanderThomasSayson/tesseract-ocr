package com.srllc.tesseract_ocr.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_voucher_redemption_form")
public class VoucherRedemptionForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Retailer name is required")
    @Size(max = 100, message = "Retailer name must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String retailerName;

    @NotBlank(message = "Barangay is required")
    @Size(max = 100, message = "Barangay must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String barangay;

    @NotBlank(message = "Municipality is required")
    @Size(max = 100, message = "Municipality must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String municipality;

    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province must be at most 100 characters")
    @Column(nullable = false, length = 100)
    private String province;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid contact number format")
    @Column(nullable = false, length = 15)
    private String contactNumber;

    @NotBlank(message = "Farm area is required")
    @Size(max = 50, message = "Farm area must be at most 50 characters")
    @Column(nullable = false, length = 50)
    private String farmArea;

    @NotBlank(message = "NK Hybrid Purchased is required")
    @Size(max = 50, message = "NK Hybrid Purchased must be at most 50 characters")
    @Column(nullable = false, length = 50)
    private String nkHybridPurchased;

    @NotBlank(message = "Number of bags purchased is required")
    @Size(max = 20, message = "Bags purchased must be at most 20 characters")
    @Column(nullable = false, length = 20)
    private String bagsPurchased;

    @NotBlank(message = "Receipt number is required")
    @Size(max = 50, message = "Receipt number must be at most 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @NotBlank(message = "Voucher number is required")
    @Size(max = 50, message = "Voucher number must be at most 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String voucherNumber;
}
