package com.srllc.tesseract_ocr.dao;

import com.srllc.tesseract_ocr.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherDAO extends JpaRepository<Voucher, Long> {
}
