package com.srllc.tesseract_ocr.domain.dao;

import com.srllc.tesseract_ocr.domain.entity.VoucherRedemptionForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRedemptionFormDAO extends JpaRepository<VoucherRedemptionForm, Long> {
}
