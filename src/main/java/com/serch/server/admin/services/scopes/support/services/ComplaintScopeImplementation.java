package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.mappers.AdminCompanyMapper;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.repositories.AdminRepository;
import com.serch.server.admin.services.scopes.support.responses.ComplaintResponse;
import com.serch.server.admin.services.scopes.support.responses.ComplaintScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.company.Complaint;
import com.serch.server.repositories.company.ComplaintRepository;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComplaintScopeImplementation implements ComplaintScopeService {
    private final ComplaintRepository complaintRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional
    public ApiResponse<List<ComplaintScopeResponse>> complaints() {
        List<Complaint> complaintList = complaintRepository.findAll();

        Map<String, List<Complaint>> groupedByEmail = complaintList.stream()
                .sorted(Comparator.comparing(Complaint::getUpdatedAt).reversed())
                .collect(Collectors.groupingBy(Complaint::getEmailAddress));

        List<ComplaintScopeResponse> responseList = groupedByEmail.entrySet().stream()
                .map(entry -> getResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new ApiResponse<>(responseList);
    }

    private ComplaintScopeResponse getResponse(String emailAddress, List<Complaint> complaints) {
        ComplaintScopeResponse response = new ComplaintScopeResponse();
        response.setEmailAddress(emailAddress);
        response.setFirstName(complaints.getFirst().getFirstName());
        response.setLastName(complaints.getFirst().getLastName());

        List<ComplaintResponse> complaintResponses = complaints.stream()
                .sorted(Comparator.comparing(Complaint::getUpdatedAt).reversed())
                .map(complaint -> {
                    ComplaintResponse c = AdminCompanyMapper.instance.response(complaint);
                    if(c.getAdmin() != null) {
                        c.getAdmin().setFirstName(complaint.getAdmin().getUser().getFirstName());
                        c.getAdmin().setLastName(complaint.getAdmin().getUser().getLastName());
                        c.getAdmin().setRole(complaint.getAdmin().getUser().getRole());
                        c.getAdmin().setEmailAddress(complaint.getAdmin().getUser().getEmailAddress());
                    }
                    return c;
                })
                .collect(Collectors.toList());

        response.setComplaints(complaintResponses);
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<List<ComplaintScopeResponse>> resolve(String id) {
        Admin admin = adminRepository.findByUser_EmailAddressIgnoreCase(AuthUtil.getAuth())
                .orElseThrow(() -> new AuthException("Admin not found"));
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new SerchException("Complaint not found"));
        complaint.setStatus(IssueStatus.RESOLVED);
        complaint.setUpdatedAt(TimeUtil.now());
        complaint.setAdmin(admin);
        complaintRepository.save(complaint);
        return complaints();
    }

    @Override
    @Transactional
    public ApiResponse<ComplaintScopeResponse> complaint(String emailAddress) {
        List<Complaint> complaintList = complaintRepository.findByEmailAddress(emailAddress);
        if(complaintList.isEmpty()) {
            throw new SerchException("Complaint not found");
        } else {
            return new ApiResponse<>(getResponse(emailAddress, complaintList));
        }
    }
}
