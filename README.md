# Tesseract OCR with Spring Boot

This project is an Optical Character Recognition (OCR) system using Tesseract, OpenCV, and Spring Boot. It allows users to upload images and extract text from them.

## Technologies Used
- **Java 21**
- **Spring Boot 3.2.3**
- **Tesseract OCR**
- **OpenCV**
- **MySQL (Optional, if storing results in a database)**

## Endpoints

### 1. Upload an Image
**POST** `/api/v1/image-processing/upload`
- Accepts an image file and processes it using Tesseract OCR.
- Returns the extracted text from the image.

### 2. Retrieve Processed Text by ID
**GET** `/api/v1/image-processing/get-by-id/{id}`
- Fetches the OCR result for a previously uploaded image using its ID.
- Requires a valid `id` parameter.

## Installation & Setup

### Prerequisites
1. **Install Tesseract OCR**
    - Linux: `sudo apt install tesseract-ocr`
    - macOS: `brew install tesseract`
    - Windows: [Download and install Tesseract](https://github.com/tesseract-ocr/tesseract)

2. **Clone the Repository**
   ```sh
   git clone https://github.com/AlexanderThomasSayson/tesseract-ocr.git
   cd tesseract-ocr-springboot
   ```

3. **Configure Application Properties**
    - Update `application.properties` with necessary database configurations if using MySQL.

4. **Build & Run the Application**
   ```sh
   mvn spring-boot:run
   ```

## Usage
- Use **Postman** or **cURL** to test the endpoints.
- Upload an image and extract text using the OCR API.

## Future Enhancements
- Add support for multiple languages in OCR.
- Implement user authentication.
- Store extracted text in a database.

## License
This project is open-source and available under the MIT License.

## Author
- **Alexander Thomas Sayson**
- Contact: alexanderthomassayson@gmail.com

