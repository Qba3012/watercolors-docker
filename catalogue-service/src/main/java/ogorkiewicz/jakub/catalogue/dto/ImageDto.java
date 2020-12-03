package ogorkiewicz.jakub.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ogorkiewicz.jakub.catalogue.model.Image;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private String original;
    private String small;

    public ImageDto(Image image) {
        this.original = image.getUrl().toString();
        this.small = image.getSmallUrl().toString();
    }
}