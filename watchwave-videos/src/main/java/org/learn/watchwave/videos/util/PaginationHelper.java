//package org.learn.watchwave.videos.util;
//
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PaginationHelper {
//
//    private static final int DEFAULT_PAGE_SIZE = 20;
//    private static final int MAX_PAGE_SIZE = 100;
//    private static final int MIN_PAGE_SIZE = 1;
//
//    public Pageable createPageable(int page, int size) {
//        return createPageable(page, size, null);
//    }
//
//    public Pageable createPageable(int page, int size, Sort sort) {
//        int validatedPage = validatePage(page);
//        int validatedSize = validatePageSize(size);
//
//        if (sort != null) {
//            return PageRequest.of(validatedPage, validatedSize, sort);
//        }
//
//        return PageRequest.of(validatedPage, validatedSize);
//    }
//
//    public Pageable createPageableWithSort(int page, int size, String sortBy, String direction) {
//        Sort.Direction sortDirection = parseSortDirection(direction);
//        Sort sort = Sort.by(sortDirection, sortBy);
//
//        return createPageable(page, size, sort);
//    }
//
//    private int validatePage(int page) {
//        return Math.max(0, page);
//    }
//
//    private int validatePageSize(int size) {
//        if (size < MIN_PAGE_SIZE) {
//            return DEFAULT_PAGE_SIZE;
//        }
//        return Math.min(size, MAX_PAGE_SIZE);
//    }
//
//    private Sort.Direction parseSortDirection(String direction) {
//        return "desc".equalsIgnoreCase(direction) ?
//                Sort.Direction.DESC : Sort.Direction.ASC;
//    }
//}
