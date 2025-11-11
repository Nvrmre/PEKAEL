package com.sistem.monitoring.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistem.monitoring.models.CompanyModel;
import com.sistem.monitoring.models.CompanySupervisorModel;
import com.sistem.monitoring.models.UserModel;
import com.sistem.monitoring.repositories.CompanyRepository;
import com.sistem.monitoring.repositories.CompanySupervisorRepository;
import com.sistem.monitoring.repositories.UserRepository;

@Service
public class CompanySupervisorService {
    
    @Autowired
    private CompanySupervisorRepository CompanySupervisorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public List<CompanySupervisorModel> getCompanySupervisor(){
        return CompanySupervisorRepository.findAll();
    }

    public Optional<CompanySupervisorModel> getCompanysupervisorById(Long id){
        return CompanySupervisorRepository.findById(id);
    }

    public CompanySupervisorModel createCompanySupervisor(CompanySupervisorModel spv){
        return CompanySupervisorRepository.save(spv);
    }

    @Transactional
    public CompanySupervisorModel updateCompanySupervisor(Long id, CompanySupervisorModel formSpv, Long companyId) {
        CompanySupervisorModel existing = CompanySupervisorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("CompanySupervisor not found: " + id));

        // --- Update nested user (safe update, bukan create) ---
        if (formSpv.getUser() != null) {
            UserModel uForm = formSpv.getUser();

            // pastikan existing user ada; kalau tidak ada, buat baru (tapi hati-hati)
            if (existing.getUser() == null) {
                existing.setUser(new UserModel());
            }
            UserModel user = existing.getUser();

            // cek email uniqueness (jika email diubah)
            String newEmail = uForm.getEmail();
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                UserModel byEmail = userRepository.findByEmail(newEmail);
                if (byEmail != null && !byEmail.getUserId().equals(user.getUserId())) {
                    throw new IllegalArgumentException("Email already used by another account");
                }
            }

            // update fields
            user.setUsername(uForm.getUsername());
            user.setEmail(uForm.getEmail());
            if (uForm.getPassword() != null && !uForm.getPassword().isEmpty()) {
                user.setPassword(uForm.getPassword());
            }

            // simpan user secara eksplisit jika relasi tidak menggunakan cascade
            userRepository.save(user);
        }

        // --- Update company relation ---
        if (companyId != null) {
            CompanyModel company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
            existing.setCompany(company);
        } else {
            existing.setCompany(null); // atau tetap biarkan existing.getCompany() jika tidak ingin mengosongkan
        }

        // --- Update jobTitle dan field lain ---
        existing.setJobTitle(formSpv.getJobTitle());

        // Simpan supervisor (ini juga akan menyimpan user jika kamu pakai cascade pada mapping)
        CompanySupervisorModel saved = CompanySupervisorRepository.save(existing);

        System.out.println("DEBUG saved cspv id=" + saved.getcSupervisorId()
                + " companyId=" + (saved.getCompany() != null ? saved.getCompany().getCompanyId() : "null")
                + " userId=" + (saved.getUser() != null ? saved.getUser().getUserId() : "null"));
        return saved;
    }


    public void deleteCompanySupervisor(Long id){
        CompanySupervisorRepository.deleteById(id);
    }
}
