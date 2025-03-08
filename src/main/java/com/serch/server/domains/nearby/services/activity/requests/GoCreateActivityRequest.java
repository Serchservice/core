package com.serch.server.domains.nearby.services.activity.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.services.account.responses.GoLocationResponse;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GoCreateActivityRequest {
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long interest;

    @JsonProperty("start_time")
    private GoCreateTime startTime;

    @JsonProperty("end_time")
    private GoCreateTime endTime;

    private GoLocationResponse location;
    private List<FileUploadRequest> images = new ArrayList<>();

    @Data
    public static class GoCreateTime {
        private Integer hour;
        private Integer minute;
    }

    public static LocalTime buildTime(GoCreateTime time) {
        if (time == null) {
            throw new SerchException("Event start time is required to create an activity");
        }

        return LocalTime.of(time.getHour(), time.getMinute());
    }
}