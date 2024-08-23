package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.company.Complaint;
import com.serch.server.repositories.company.ComplaintRepository;
import com.serch.server.services.company.requests.ComplaintRequest;
import com.serch.server.services.company.services.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintImplementation implements ComplaintService {
    private final ComplaintRepository complaintRepository;

    @Override
    public ApiResponse<String> complain(ComplaintRequest request) {
        Complaint complaint = CompanyMapper.INSTANCE.complaint(request);
        complaintRepository.save(complaint);
        return new ApiResponse<>("Complaint received", HttpStatus.CREATED);
    }

    @Override
    public void removeOldContents() {
        LocalDateTime date = LocalDateTime.now().minusYears(5);
        List<Complaint> list = complaintRepository.findByCreatedAtBefore(date);
        if(list != null && !list.isEmpty()) {
            complaintRepository.deleteAll(list);
        }
    }
}