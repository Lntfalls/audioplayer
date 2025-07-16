package com.example.menshih;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class MusicController {

    private final SongRepository songRepository;

    public MusicController(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("songs", songRepository.findAll());
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("title") String title,
                         @RequestParam("file") MultipartFile file) throws IOException {

        Song song = new Song();
        song.setTitle(title);
        song.setFilename(file.getOriginalFilename());
        song.setContent(file.getBytes());

        songRepository.save(song);
        return "redirect:/";
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<ByteArrayResource> streamAudio(@PathVariable Long id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(song.getContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + song.getFilename() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .contentLength(song.getContent().length)
                .body(resource);
    }
}