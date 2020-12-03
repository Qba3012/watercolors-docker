package ogorkiewicz.jakub.catalogue.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tag")
public class Tag {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToMany(mappedBy = "tags")
    private Set<Painting> paintings = new HashSet<>();

    public Tag (String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null) 
            return false;
        if(getClass() != obj.getClass())
            return false;
        Tag other = (Tag) obj;
        if(this.name.equals(other.getName())) {
            return true;
        } else {
            return false;
        }
    }   
    
}