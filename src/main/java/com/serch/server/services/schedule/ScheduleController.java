package com.serch.server.services.schedule;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.services.schedule.requests.ScheduleRequest;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;
import com.serch.server.services.schedule.services.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {
    private final ScheduleService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> schedules() {
        ApiResponse<List<ScheduleResponse>> response = service.schedules();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/today")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> today() {
        ApiResponse<List<ScheduleResponse>> response = service.today();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all/times/{provider}")
    public ResponseEntity<ApiResponse<List<ScheduleTimeResponse>>> times(@PathVariable("provider") UUID provider) {
        ApiResponse<List<ScheduleTimeResponse>> response = service.times(provider);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<String>> request(@RequestBody ScheduleRequest request) {
        ApiResponse<String> response = service.request(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/decline")
    public ResponseEntity<ApiResponse<String>> decline(@RequestBody ScheduleDeclineRequest decline) {
        ApiResponse<String> response = service.decline(decline);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/accept/{id}")
    public ResponseEntity<ApiResponse<String>> accept(@PathVariable("id") String id) {
        ApiResponse<String> response = service.accept(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable("id") String id) {
        ApiResponse<String> response = service.cancel(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/close/{id}")
    public ResponseEntity<ApiResponse<String>> close(@PathVariable("id") String id) {
        ApiResponse<String> response = service.close(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/start/{id}")
    public ResponseEntity<ApiResponse<String>> start(@PathVariable("id") String id) {
        ApiResponse<String> response = service.start(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
