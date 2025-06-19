package com.practice.review.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ImageMetadata {
    @Id
    private String filename;
    private String ownerUid;

    public ImageMetadata() {}

    public ImageMetadata(String filename, String ownerUid) {
        this.filename = filename;
        this.ownerUid = ownerUid;
    }


}
