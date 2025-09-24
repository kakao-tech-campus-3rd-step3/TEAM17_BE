package com.starterpack.linkpolicy.service;

import com.starterpack.linkpolicy.dto.LinkPolicyCreateRequestDto;
import com.starterpack.linkpolicy.dto.LinkPolicyResponseDto;
import com.starterpack.linkpolicy.model.LinkPolicy;
import com.starterpack.linkpolicy.repository.LinkPolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LinkPolicyService {

    private final LinkPolicyRepository repository;

    public LinkPolicyService(LinkPolicyRepository repository) {
        this.repository = repository;
    }

    public LinkPolicyResponseDto add(LinkPolicyCreateRequestDto request) {
        LinkPolicy saved = repository.save(new LinkPolicy(request.pattern(), java.time.LocalDateTime.now()));
        return LinkPolicyResponseDto.from(saved);
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public List<LinkPolicyResponseDto> getAll() {
        return repository.findAll().stream()
                .map(LinkPolicyResponseDto::from)
                .collect(Collectors.toList());
    }
}


