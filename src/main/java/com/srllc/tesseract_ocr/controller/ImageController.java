package com.srllc.tesseract_ocr.controller;

import com.srllc.tesseract_ocr.dto.ImageDto;
import com.srllc.tesseract_ocr.service.ImageProcessingService;
import com.srllc.tesseract_ocr.utils.ApiResponse;
import com.srllc.tesseract_ocr.utils.DefaultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/image-processing")
@Tag(name = "Image Processing Controller", description = "Operations for managing text extract using tesseracts and open-cv")
public class ImageController {

    private final ImageProcessingService imageProcessingService;

    public ImageController(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    @Operation(summary = "Upload and Process Image", description = "This endpoint allows uploading and processing of images.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ImageDto> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {
        ImageDto imageDto = imageProcessingService.processAndSaveImage(file);
        return DefaultResponse.displayCreatedObject(imageDto);
    }


    @GetMapping("/get-by-id/{id}")
    @Operation(summary = "Get image by ID", description = "This endpoint retrieves image by ID.")
    public ApiResponse<ImageDto> getImageByID(@PathVariable("id") Long id){
        ImageDto imageDto = imageProcessingService.getImageById(id);
        return DefaultResponse.displayFoundObject(imageDto);
    }
}
