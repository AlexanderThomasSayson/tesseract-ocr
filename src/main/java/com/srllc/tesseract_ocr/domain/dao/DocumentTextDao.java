package com.srllc.tesseract_ocr.domain.dao;

import com.srllc.tesseract_ocr.domain.entity.DocumentText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentTextDao extends JpaRepository<DocumentText, Long> {
}
