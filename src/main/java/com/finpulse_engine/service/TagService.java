package com.finpulse_engine.service;

import com.finpulse_engine.dto.request.UpdateExpenseTagRequest;
import com.finpulse_engine.entity.ExpenseTag;
import com.finpulse_engine.repository.ExpenseTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TagService {

    @Autowired
    private ExpenseTagRepository expenseTagRepository;

    public List<String> addTags(UUID categoryId, List<String> addTags) {
        List<String> failedAddTags = new ArrayList<>();
        if (addTags == null || addTags.isEmpty()) {
            return failedAddTags;
        }

        for (String tag : addTags) {
            if (!this.expenseTagRepository.existsByCategoryIdAndName(categoryId, tag)) {
                ExpenseTag expenseTag = ExpenseTag.builder()
                        .name(tag.trim().toLowerCase())
                        .categoryId(categoryId)
                        .build();
                this.expenseTagRepository.save(expenseTag);
            } else {
                failedAddTags.add(tag);
            }
        }
        return failedAddTags;
    }


    public List<String> updateTags(List<UpdateExpenseTagRequest> updateTagRequests) {
        List<String> failedUpdateTags = new ArrayList<>();
        if (updateTagRequests == null || updateTagRequests.isEmpty()) {
            return failedUpdateTags;
        }

        for (UpdateExpenseTagRequest updateTagRequest : updateTagRequests) {
            Optional<ExpenseTag> expenseTag = this.expenseTagRepository.findById(UUID.fromString(updateTagRequest.getId()));
            if (expenseTag.isPresent()) {
                this.expenseTagRepository.updateTagNameById(
                        UUID.fromString(expenseTag.get().getId().toString()),
                        updateTagRequest.getNewName()
                );
            } else {
                failedUpdateTags.add(updateTagRequest.getNewName());
            }
        }

        return failedUpdateTags;
    }


}
