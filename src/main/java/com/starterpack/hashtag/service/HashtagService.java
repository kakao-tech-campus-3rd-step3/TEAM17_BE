package com.starterpack.hashtag.service;


import com.starterpack.hashtag.entity.Hashtag;
import com.starterpack.hashtag.repository.HashtagRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HashtagService {
    private final HashtagRepository hashtagRepository;

    @Transactional
    public List<Hashtag> resolveHashtags(List<String> hashtagNames) {
        if (hashtagNames == null) {
            return null;
        }

        if (hashtagNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> normalizedNames = normalizeHashtagNames(hashtagNames);

        Set<Hashtag> hashtags = findOrAddHashtags(normalizedNames);

        Map<String, Hashtag> hashtagMap = hashtags.stream()
                .collect(Collectors.toMap(Hashtag::getName, Function.identity()));

        return normalizedNames.stream()
                .map(hashtagMap::get)
                .collect(Collectors.toList());
    }

    @Transactional
    public void incrementUsageCount(Set<Hashtag> hashtags) {
        if (hashtags == null || hashtags.isEmpty()) return;

        Set<Long> ids = hashtags.stream()
                .map(Hashtag::getId)
                .collect(Collectors.toSet());

        hashtagRepository.bulkIncrementUsageCount(ids);
    }

    @Transactional
    public void decrementUsageCount(Set<Hashtag> hashtags) {
        if (hashtags == null || hashtags.isEmpty()) return;

        Set<Long> ids = hashtags.stream()
                .map(Hashtag::getId)
                .collect(Collectors.toSet());

        hashtagRepository.bulkDecrementUsageCount(ids);
    }

    private List<String> normalizeHashtagNames(List<String> hashtagNames) {
        return hashtagNames.stream()
                .map(String::toLowerCase)
                .distinct()
                .toList();
    }

    private Set<Hashtag> findOrAddHashtags(List<String> normalizedNames) {
        Set<Hashtag> existingTags = hashtagRepository.findAllByNameIn(new HashSet<>(normalizedNames));

        Set<String> existingTagNames = existingTags.stream().map(Hashtag::getName).collect(Collectors.toSet());

        Set<Hashtag> newTags = normalizedNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(Hashtag::new)
                .collect(Collectors.toSet());

        hashtagRepository.saveAll(newTags);

        existingTags.addAll(newTags);

        return existingTags;
    }
}
