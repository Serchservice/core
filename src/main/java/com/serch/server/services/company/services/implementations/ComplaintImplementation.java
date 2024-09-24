package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.company.Complaint;
import com.serch.server.repositories.company.ComplaintRepository;
import com.serch.server.services.company.requests.ComplaintRequest;
import com.serch.server.services.company.services.ComplaintService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class ComplaintImplementation implements ComplaintService {
    private final ComplaintRepository complaintRepository;

    @Override
    public ApiResponse<String> complain(ComplaintRequest request) {
        Complaint complaint = CompanyMapper.INSTANCE.complaint(request);
        complaintRepository.save(complaint);
        return new ApiResponse<>("Complaint received", HttpStatus.CREATED);
    }

    @Override
    @Transactional
    public void removeOldContents() {
        List<Complaint> list = complaintRepository.findByCreatedAtBefore(TimeUtil.now().minusYears(5));
        if(list != null && !list.isEmpty()) {
            complaintRepository.deleteAll(list);
        }
    }
}