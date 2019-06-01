package com.jordantymburski.driftoff.domain;

import com.jordantymburski.driftoff.domain.usecase.SetInfo;
import com.jordantymburski.driftoff.domain.usecase.StopAudio;

import javax.inject.Inject;

/**
 * Direct access to injected domain use cases. Requires that the main component is fetched and
 * inject is called to set the internal use cases
 */
public class DomainProvider {
    @Inject
    public SetInfo setInfo;

    @Inject
    public StopAudio stopAudio;

    public DomainProvider() {}
}
