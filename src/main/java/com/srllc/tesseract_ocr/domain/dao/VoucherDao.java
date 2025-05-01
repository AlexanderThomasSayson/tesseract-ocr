package com.srllc.tesseract_ocr.domain.dao;

import com.srllc.tesseract_ocr.domain.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherDao extends JpaRepository<Voucher, Long> {
}
