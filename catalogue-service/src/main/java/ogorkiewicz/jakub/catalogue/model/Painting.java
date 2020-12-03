package ogorkiewicz.jakub.catalogue.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "painting")
public class Painting {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String collection;
    private String description;
    private String category;
    private Availability availability;
    private int width;
    private int height;
    private Double price;
    @Column(name ="create_date_time")
    private OffsetDateTime createDateTime;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "painting")
    private List<Image> images;
    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
        name = "paintings_tags",
        joinColumns = @JoinColumn(name = "painting_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @PrePersist
    private void initCreateDate() {
        this.createDateTime = OffsetDateTime.now();
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getPaintings().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getPaintings().remove(this);
    }

}