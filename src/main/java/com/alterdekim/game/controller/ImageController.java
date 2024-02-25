package com.alterdekim.game.controller;

import com.alterdekim.game.entities.Image;
import com.alterdekim.game.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
class ImageController {

    @Autowired
    ImageRepository imageRepository;

    @PostMapping(value = "/image/upload/")
    private Long uploadImage(@RequestParam MultipartFile multipartImage) throws Exception {
        Image dbImage = new Image();
        dbImage.setContent(multipartImage.getBytes());
        return imageRepository.save(dbImage)
                .getId();
    }

    @GetMapping(value = "/image/store/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long imageId) {
        byte[] image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
                .getContent();

        return new ByteArrayResource(image);
    }
}