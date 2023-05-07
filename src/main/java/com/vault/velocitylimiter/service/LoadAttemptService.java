package com.vault.velocitylimiter.service;

import com.vault.velocitylimiter.model.LoadAttempt;

public interface LoadAttemptService {
    LoadAttempt processLoadAttempt(LoadAttempt loadAttempt);
}
