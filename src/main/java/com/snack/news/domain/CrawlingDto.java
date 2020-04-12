package com.snack.news.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
@AllArgsConstructor
public class CrawlingDto {
    private String title;
    private String link;
    private String description;
    private LocalDateTime publishAt;
}
