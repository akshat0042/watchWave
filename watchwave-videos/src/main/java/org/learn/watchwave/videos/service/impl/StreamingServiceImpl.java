package org.learn.watchwave.videos.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.learn.watchwave.videos.model.entity.Video;
import org.learn.watchwave.videos.repository.VideoRepository;
import org.learn.watchwave.videos.service.StreamingService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamingServiceImpl implements StreamingService {

    private final VideoRepository videoRepository;

    @Override
    public ResponseEntity<Resource> streamVideo(UUID videoId, String rangeHeader) {
        try {
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

            if (video.getIsDeleted() || !video.getProcessingStatus().toString().equals("READY")) {
                return ResponseEntity.notFound().build();
            }

            Path videoPath = Paths.get(video.getVideoFilePath());
            if (!Files.exists(videoPath)) {
                log.error("Video file not found: {}", video.getVideoFilePath());
                return ResponseEntity.notFound().build();
            }

            long fileSize = Files.size(videoPath);
            long start = 0;
            long end = fileSize - 1;
            boolean hasRange = false;

            // Parse Range header if present
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                hasRange = true;
                String[] ranges = rangeHeader.substring(6).split("-");
                start = Long.parseLong(ranges[0]);
                end = (ranges.length > 1 && !ranges[1].isEmpty())
                        ? Long.parseLong(ranges[1])
                        : fileSize - 1;
                end = Math.min(end, fileSize - 1);
            }

            // Validate range
            if (hasRange && (start < 0 || start > end || end >= fileSize)) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
            }

            long contentLength = end - start + 1;

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(determineContentType(video.getVideoFilePath())));
            headers.set("Accept-Ranges", "bytes");
            headers.setContentLength(contentLength);

            if (hasRange) {
                headers.set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            }

            // Use RandomAccessFile for efficient seeking
            RandomAccessFile file = new RandomAccessFile(videoPath.toFile(), "r");
            file.seek(start);

            InputStream inputStream = new InputStream() {
                @Override
                public int read() throws IOException {
                    return file.read();
                }

                @Override
                public void close() throws IOException {
                    file.close();
                }
            };

            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.status(hasRange ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK)
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error("Error streaming video: {}", videoId, e);
            return ResponseEntity.internalServerError().build();
        }
    }


//    @Override
//    public ResponseEntity<Resource> streamVideo(UUID videoId, String range) {
//        try {
//            // Get video from database
//            Video video = videoRepository.findById(videoId)
//                    .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));
//
//            // Check if video is deleted or not ready
//            if (video.getIsDeleted() || !video.getProcessingStatus().toString().equals("READY")) {
//                return ResponseEntity.notFound().build();
//            }
//
//            // Get file from file system
//            Path videoPath = Paths.get(video.getVideoFilePath());
//            if (!Files.exists(videoPath)) {
//                log.error("Video file not found: {}", video.getVideoFilePath());
//                return ResponseEntity.notFound().build();
//            }
//
//            Resource videoResource = new FileSystemResource(videoPath);
//
//            // Determine content type
//            String contentType = determineContentType(video.getVideoFilePath());
//
//            // Build response headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType(contentType));
//            headers.set("Accept-Ranges", "bytes");
//            headers.set("Cache-Control", "no-cache, no-store, must-revalidate");
//            headers.set("Pragma", "no-cache");
//            headers.set("Expires", "0");
//
//            log.info("Streaming video: {} ({})", video.getTitle(), contentType);
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(videoResource);
//
//        } catch (Exception e) {
//            log.error("Error streaming video: {}", videoId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    @Override
    public ResponseEntity<Resource> getThumbnail(UUID videoId) {
        try {
            // Get video from database
            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new RuntimeException("Video not found: " + videoId));

            // Check if thumbnail exists
            if (video.getThumbnailFilePath() == null) {
                return ResponseEntity.notFound().build();
            }

            // Get thumbnail file
            Path thumbnailPath = Paths.get(video.getThumbnailFilePath());
            if (!Files.exists(thumbnailPath)) {
                log.error("Thumbnail file not found: {}", video.getThumbnailFilePath());
                return ResponseEntity.notFound().build();
            }

            Resource thumbnailResource = new FileSystemResource(thumbnailPath);

            // Determine content type
            String contentType = video.getThumbnailContentType() != null ?
                    video.getThumbnailContentType() : "image/jpeg";

            // Build response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.set("Cache-Control", "public, max-age=2592000"); // Cache for 1 month

            log.info("Serving thumbnail for video: {}", video.getTitle());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(thumbnailResource);

        } catch (Exception e) {
            log.error("Error serving thumbnail: {}", videoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String determineContentType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "mp4" -> "video/mp4";
            case "avi" -> "video/x-msvideo";
            case "mov" -> "video/quicktime";
            case "mkv" -> "video/x-matroska";
            case "webm" -> "video/webm";
            default -> "video/mp4"; // Default fallback
        };
    }
}
