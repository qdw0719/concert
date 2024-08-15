package com.hb.concert.infrastructure.dataplatform;

import com.hb.concert.domain.dataplatform.DataPlatformFailed;
import com.hb.concert.domain.dataplatform.repository.DataPlatformFailedRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataPlatformFailedRepositoryImpl implements DataPlatformFailedRepository {

    private final DataPlatformFailedJpaRepository dataPlatformFailedJpaRepository;

    public DataPlatformFailedRepositoryImpl(DataPlatformFailedJpaRepository dataPlatformFailedJpaRepository) {
        this.dataPlatformFailedJpaRepository = dataPlatformFailedJpaRepository;
    }

    @Override public void save(DataPlatformFailed failed) {
        dataPlatformFailedJpaRepository.save(failed);
    }

    @Override public List<DataPlatformFailed> getResendTargetList() {
        return dataPlatformFailedJpaRepository.resendTargetList();
    }
}
