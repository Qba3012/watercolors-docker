package ogorkiewicz.jakub.catalogue.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ogorkiewicz.jakub.catalogue.dto.DetailedPaintingDto;
import ogorkiewicz.jakub.catalogue.dto.SimplePaintingDto;
import ogorkiewicz.jakub.catalogue.exception.BadRequestException;
import ogorkiewicz.jakub.catalogue.exception.ErrorCode;
import ogorkiewicz.jakub.catalogue.model.Availability;
import ogorkiewicz.jakub.catalogue.model.Painting;
import ogorkiewicz.jakub.catalogue.model.Tag;
import ogorkiewicz.jakub.catalogue.repository.PaintingRepository;
import ogorkiewicz.jakub.catalogue.repository.TagRepository;

@Service
public class PaintingService {
    
    private PaintingRepository paintingRepository;
    private TagRepository tagRepository;

    @Autowired
    public PaintingService (PaintingRepository paintingRepository, TagRepository tagRepository) {
        this.paintingRepository = paintingRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<SimplePaintingDto> getPaintings() {
        List<Painting> paintings = paintingRepository.findAll();
        return paintings.stream().map(SimplePaintingDto::new).collect(Collectors.toList());
    }

    public DetailedPaintingDto getPaintingById(Long id) throws BadRequestException{
        return new DetailedPaintingDto(getPainting(id));
    }

    @Transactional
    public DetailedPaintingDto updatePainting(DetailedPaintingDto detailedPaintingDto) {

        //Updating general part
        Painting painting = getPainting(detailedPaintingDto.getId());
        if(detailedPaintingDto.getTitle() != null) {painting.setTitle(detailedPaintingDto.getTitle());};
        if(detailedPaintingDto.getDescription() != null) {painting.setDescription(detailedPaintingDto.getDescription());};
        if(detailedPaintingDto.getAvailability() != null) {painting.setAvailability(Availability.valueOf(detailedPaintingDto.getAvailability()));};
        if(detailedPaintingDto.getCategory() != null) {painting.setCategory(detailedPaintingDto.getCategory());};
        if(detailedPaintingDto.getHeight() != 0) {painting.setHeight(detailedPaintingDto.getHeight());};
        if(detailedPaintingDto.getWidth() != 0) {painting.setWidth(detailedPaintingDto.getWidth());};
        if(detailedPaintingDto.getPrice() != null) {painting.setPrice(detailedPaintingDto.getPrice());};

        List<Tag> updateTags = detailedPaintingDto.getTags().stream().map(Tag::new).collect(Collectors.toList());
        List<Tag> deleteTags = new ArrayList<>();
        List<Tag> newTags = new ArrayList<>();

        // Adding new tags
        for(Tag tag : updateTags) {
            if(!painting.getTags().contains(tag)) {
                newTags.add(tag);
            }
        }
        painting.getTags().addAll(newTags);
        paintingRepository.flush();

        // Deleting new tags
        for(Tag tag : painting.getTags()) {
            if(!updateTags.contains(tag)) {
                deleteTags.add(tag);
            } 
        }
        painting.getTags().removeAll(deleteTags);
        paintingRepository.flush();

        // Purging tags with no reference to painting
        tagRepository.deleteAll(deleteTags.stream().filter(t -> t.getPaintings().isEmpty()).collect(Collectors.toList()));

        return new DetailedPaintingDto(painting);
    }

    @Transactional
	public void deletePaintingById(Long id) {
        Optional<Painting> paintingOptional = paintingRepository.findById(id);

        if(paintingOptional.isPresent()) {
            Painting painting = paintingOptional.get();
            tagRepository.deleteAll(painting.getTags().stream().filter(t -> t.getPaintings().size() == 1).collect(Collectors.toList()));
            paintingRepository.delete(painting);
        }
    }
    
    @Transactional(readOnly = true)
    private Painting getPainting(Long id) {
        Optional<Painting> painting = paintingRepository.findById(id);
        if( painting.isPresent()) {
            return painting.get();
        } else {
            throw new BadRequestException(ErrorCode.NOT_FOUND, Painting.class);
        }
    }

    @Transactional
	public DetailedPaintingDto savePainting(DetailedPaintingDto paintingDto) {
        Painting painting = paintingDto.toEntity();

        for(String tagName : paintingDto.getTags()) {
            Optional<Tag> tagDb = tagRepository.findByName(tagName);
            if(tagDb.isPresent()) {
                painting.addTag(tagDb.get());
            } else {
                painting.addTag(new Tag(tagName));
            }
        }

        paintingRepository.save(painting);
		return new DetailedPaintingDto(painting);
	}

}