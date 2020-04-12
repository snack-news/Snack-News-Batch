package com.snack.news.job;

import com.apptastic.rssreader.Item;
import com.apptastic.rssreader.RssReader;
import com.snack.news.domain.CrawlingDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Crawling {
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;

    public Crawling(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job crawlingJob(){
        return jobBuilderFactory.get("crawling")
                .start(crawlingStep())
                .build();
    }

    @Bean
    public Step crawlingStep(){
        return stepBuilderFactory.get("crawlingStep")
                .tasklet(((contribution, chunkContext) -> {
                    RssReader reader = new RssReader();
                    Stream<Item> rssFeed = reader.read("https://techneedle.com/feed");

                    List<CrawlingDto> crawlingDtos = rssFeed.map(item -> CrawlingDto.builder()
                            .title(item.getTitle().orElse("EMPTY TITLE"))
                            .link(item.getLink().orElse("EMPTY LINK"))
                            .description(item.getDescription().orElse("EMPTY DESCRIPTION"))
                            .publishAt(LocalDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME.parse(item.getPubDate().orElse("EMPTY PUBLISH"))))
                            .build()).collect(Collectors.toList());

                    System.out.println(crawlingDtos);

                    return RepeatStatus.FINISHED;
                }))
                .build();
    }
}
