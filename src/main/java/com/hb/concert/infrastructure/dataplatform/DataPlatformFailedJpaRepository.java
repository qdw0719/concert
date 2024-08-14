package com.hb.concert.infrastructure.dataplatform;

import com.hb.concert.domain.dataplatform.DataPlatformFailed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DataPlatformFailedJpaRepository extends JpaRepository<DataPlatformFailed, Long> {

    @Query("select f from DataPlatformFailed f where f.resendYn = 'N'")
    List<DataPlatformFailed> resendTargetList();
}
