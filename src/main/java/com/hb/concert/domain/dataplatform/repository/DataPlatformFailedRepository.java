package com.hb.concert.domain.dataplatform.repository;

import com.hb.concert.domain.dataplatform.DataPlatformFailed;

import java.util.List;

public interface DataPlatformFailedRepository {
    void save(DataPlatformFailed failed);

    List<DataPlatformFailed> getResendTargetList();
}
