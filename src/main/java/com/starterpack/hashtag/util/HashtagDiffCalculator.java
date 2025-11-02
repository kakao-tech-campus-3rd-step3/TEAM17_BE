package com.starterpack.hashtag.util;

import com.starterpack.hashtag.dto.HashtagUpdateResult;
import com.starterpack.hashtag.entity.Hashtag;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HashtagDiffCalculator {
    public static HashtagUpdateResult calculateDiff(Collection<Hashtag> oldTags, Collection<Hashtag> newTags) {
        if (newTags == null || newTags.isEmpty()) {
            if (oldTags == null || oldTags.isEmpty()) {
                return HashtagUpdateResult.EMPTY_HASHTAG;
            }

            return new HashtagUpdateResult(Collections.emptySet(), new HashSet<>(oldTags));
        }

        if (oldTags == null || oldTags.isEmpty()) {
            return new HashtagUpdateResult(new HashSet<>(newTags), Collections.emptySet());
        }

        Set<Hashtag> oldSet = new HashSet<>(oldTags);
        Set<Hashtag> newSet = new HashSet<>(newTags);

        Set<Hashtag> added = new HashSet<>(newSet);
        added.removeAll(oldSet);

        Set<Hashtag> removed = new HashSet<>(oldSet);
        removed.removeAll(newSet);

        return new HashtagUpdateResult(added, removed);
    }
}
