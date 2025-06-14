//package org.learn.watchwave.videos.util;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ResponseHelper {
//
//    public <T> ResponseEntity<T> created(T body) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(body);
//    }
//
//    public <T> ResponseEntity<T> ok(T body) {
//        return ResponseEntity.ok(body);
//    }
//
//    public ResponseEntity<Void> noContent() {
//        return ResponseEntity.noContent().build();
//    }
//
//    public ResponseEntity<Void> notFound() {
//        return ResponseEntity.notFound().build();
//    }
//
//    public <T> ResponseEntity<T> badRequest(T body) {
//        return ResponseEntity.badRequest().body(body);
//    }
//}
