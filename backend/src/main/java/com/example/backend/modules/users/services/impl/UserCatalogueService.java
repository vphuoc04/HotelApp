package com.example.backend.modules.users.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.modules.users.entities.UserCatalogue;
import com.example.backend.modules.users.repositories.UserCatalogueRepository;
import com.example.backend.modules.users.requests.UserCatalogue.StoreRequest;
import com.example.backend.modules.users.requests.UserCatalogue.UpdateRequest;
import com.example.backend.modules.users.services.interfaces.UserCatalogueServiceInterface;
import com.example.backend.services.BaseService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserCatalogueService extends BaseService implements UserCatalogueServiceInterface {
    @Autowired
    private UserCatalogueRepository userCataloguesRepository;

    @Override
    @Transactional
    public UserCatalogue create(StoreRequest request, Long addedBy) {
        try {
            UserCatalogue payload = UserCatalogue.builder()
                .name(request.getName())
                .publish(request.getPublish())
                .addedBy(addedBy)
                .build();

            return userCataloguesRepository.save(payload);
        } catch (Exception e) {
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }  

    @Override
    @Transactional
    public UserCatalogue update(Long id, UpdateRequest request, Long editedBy) {
        UserCatalogue userCatalogue = userCataloguesRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));
        
        UserCatalogue payload = userCatalogue.toBuilder()
            .name(request.getName())
            .publish(request.getPublish())
            .editedBy(editedBy)
            .build();    

        return userCataloguesRepository.save(payload);
    }

    @Override
    public Page<UserCatalogue> paginate(Map<String, String[]> parameters) {
        int page = parameters.containsKey("page") ? Integer.parseInt(parameters.get("page")[0]) : 1;
        int perpage = parameters.containsKey("perpage") ? Integer.parseInt(parameters.get("perpage")[0]) : 10;
        String sortParam = parameters.containsKey("sort") ? parameters.get("sort")[0] : null;
        Sort sort = createSort(sortParam);

        Pageable pageable = PageRequest.of(page - 1, perpage, sort);

        return userCataloguesRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        UserCatalogue userCatalogue = userCataloguesRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User catalogue not found"));

        userCataloguesRepository.delete(userCatalogue);

        return true;
    }
}