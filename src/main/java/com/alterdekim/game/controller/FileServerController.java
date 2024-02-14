package com.alterdekim.game.controller;

import com.alterdekim.game.storage.StorageFileNotFoundException;
import com.alterdekim.game.storage.StorageProperties;
import com.alterdekim.game.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableConfigurationProperties(StorageProperties.class)
public class FileServerController {

    private final StorageService storageService;

    @Autowired
    public FileServerController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/static/{assetspath}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename, @PathVariable String assetspath) {
        Resource file = storageService.loadAsResource("static/" + assetspath + "/" + filename);
        if( assetspath.equals("images") ) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        }
        return ResponseEntity.ok().contentType(new MediaType("text", assetspath)).header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}

