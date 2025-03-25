package com.srllc.tesseract_ocr.dao;

import com.srllc.tesseract_ocr.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageDao extends JpaRepository<Image, Long> {
}
