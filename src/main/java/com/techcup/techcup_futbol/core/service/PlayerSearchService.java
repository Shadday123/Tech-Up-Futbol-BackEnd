package com.techcup.techcup_futbol.core.service;

import com.techcup.techcup_futbol.Controller.dto.PlayerSearchRequest;
import com.techcup.techcup_futbol.Controller.dto.PlayerSearchResult;

import java.util.List;

public interface PlayerSearchService {
    List<PlayerSearchResult> search(PlayerSearchRequest filters);
}
