package com.starterpack.hashtag.dto;

import com.starterpack.hashtag.entity.Hashtag;
import java.util.Collections;
import java.util.Set;

public record HashtagUpdateResult(
        Set<Hashtag> added,
        Set<Hashtag> removed
){
    public static final HashtagUpdateResult EMPTY_HASHTAG = new HashtagUpdateResult(Collections.emptySet(), Collections.emptySet());
}
