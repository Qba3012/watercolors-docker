package ogorkiewicz.jakub.catalogue.service;
import static ogorkiewicz.jakub.catalogue.resource.ImageController.IMAGE_PATH;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.catalogue.dto.ImageDto;
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.model.Image;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.repository.ImageRepository;
import ogorkiewicz.jakub.catalogue.repository.PaintingRepository;

@Service
@JBossLog
public class ImageService {

    @Value("${images.server}")
    private String homeUrl;
    
    @Value("${spring.mvc.servlet.path}")
    private String api;

    private ImageRepository imageRepository;
    private PaintingRepository paintingRepository;

    private FileService fileService;

    @Autowired
    public ImageService(ImageRepository imageRepository, FileService fileService, PaintingRepository paintingRepository) {
        this.imageRepository = imageRepository;
        this.fileService = fileService;
        this.paintingRepository = paintingRepository;
    }

    @Transactional
    public void saveImages(MultipartFile files[], Long paintingId) {

        // Check if painting with given id exist
        Optional<Painting> paintingOptional = paintingRepository.findById(paintingId);

        if(!paintingOptional.isPresent()) {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Painting.class);
        }
        Painting painting = paintingOptional.get();

        // Create list of files names already assigned to painting
        List<String> imagesNames = painting.getImages().stream().map(Image::getFileName).collect(Collectors.toList());

        // Save new images
        for (MultipartFile mpf : files) {
            Path filePath = fileService.saveFile(mpf, paintingId);
            Path smallFilePath = fileService.resizeFile(filePath, paintingId);
            
            // Persist data if file not already exists
            if(!imagesNames.contains(mpf.getOriginalFilename()) && filePath != null) {
                Image image = new Image();
                image.setFileName(mpf.getOriginalFilename());
                image.setLocalUri(filePath.toUri());
                image.setSmallLocalUri(smallFilePath.toUri());
                String smallFileName = FilenameUtils.getName(smallFilePath.toString());
                image.setSmallFileName(smallFileName);
                try {
                    image.setUrl(new URL(homeUrl + api + IMAGE_PATH + "/" + paintingId + "/" + mpf.getOriginalFilename()));
                    image.setSmallUrl(new URL(homeUrl + api + IMAGE_PATH + "/" + paintingId + "/small/" + smallFileName));
                } catch (MalformedURLException e) {
                    log.error("Unable to create file url" + e.getMessage());
                }
                image.setPainting(painting);
                imageRepository.save(image);
            }
        }

    }

    @Transactional(readOnly = true)
	public InputStream getImageByFilename(String fileName, Long paintingId) {
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            return fileService.readFile(image.get().getLocalUri());
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Image.class);
        }
	}

    @Transactional(readOnly = true)
	public InputStream getSmallImageByFilename(String fileName, Long paintingId) {
        Optional<Image> image = imageRepository.findBySmallFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            return fileService.readFile(image.get().getSmallLocalUri());
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Image.class);
        }
    }
    
    @Transactional(readOnly = true)
	public List<ImageDto> getImagesByPaintingId(Long paintingId) {
        List<Image> images = imageRepository.findByPaintingId(paintingId);
        if(images == null) {
            images = Collections.emptyList();
        }
        return images.stream().map(ImageDto::new).collect(Collectors.toList());
	}

    @Transactional
	public void deleteImage(Long paintingId, String fileName) {
        Optional<Image> image = imageRepository.findByFileNameAndPaintingId(fileName, paintingId);
        if(image.isPresent()){
            Image imageEntity = image.get();
            fileService.deleteFile(Paths.get(imageEntity.getLocalUri()));
            fileService.deleteFile(Paths.get(imageEntity.getSmallLocalUri()));
            imageRepository.delete(imageEntity);
        }
    }
    
    @Transactional
	public void deleteAllImage(Long paintingId) {
        fileService.deleteAllPaintingImages(paintingId);
        imageRepository.deleteByPaintingId(paintingId);
	}

}