package com.hb.concert.infrastructure.concert;

import com.hb.concert.domain.concert.ConcertDetail;
import com.hb.concert.domain.concert.ConcertReservation;
import com.hb.concert.domain.concert.ConcertSeatConfig;
import com.hb.concert.domain.concert.ViewData.ScheduleInfo;
import com.hb.concert.domain.concert.ViewData.ConcertInfo;
import com.hb.concert.domain.concert.ViewData.SeatInfo;
import com.hb.concert.domain.concert.Concert;
import com.hb.concert.domain.concert.repository.ConcertRepository;
import com.hb.concert.domain.exception.CustomException.NotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertDetailJpaRepository concertDetailJpaRepository;
    private final ConcertSeatConfigJpaRepository concertSeatConfigJpaRepository;
    private final ConcertReservationJpaRepository concertReservationJpaRepository;

    public ConcertRepositoryImpl(ConcertJpaRepository concertJpaRepository, ConcertDetailJpaRepository concertDetailJpaRepository, ConcertSeatConfigJpaRepository concertSeatConfigJpaRepository, ConcertReservationJpaRepository concertReservationJpaRepository) {
        this.concertJpaRepository = concertJpaRepository;
        this.concertDetailJpaRepository = concertDetailJpaRepository;
        this.concertSeatConfigJpaRepository = concertSeatConfigJpaRepository;
        this.concertReservationJpaRepository = concertReservationJpaRepository;
    }

    @Override public List<ConcertInfo> getConcertInfo() {
        return concertDetailJpaRepository.findByAvailableConcerts().stream()
                .map(concertDetail -> {
                    Concert concert = concertJpaRepository.findByConcertId(concertDetail.getConcertId()).orElseThrow(() -> new NotFoundException(NotFoundException.CONCERT_NOT_FOUND));
                    return new ConcertInfo(
                            concert.getConcertId(),
                            concertDetail.getConcertDetailId(),
                            concert.getConcertName(),
                            concert.getArtist(),
                            concertDetail.getConcertDate(),
                            concertDetail.getAvailableSeatCount()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override public ScheduleInfo getScheduleInfo(String concertId) {
        return new ScheduleInfo(concertDetailJpaRepository.findByAvailableConcerts());
    }

    @Override public SeatInfo getSeatInfo(String concertId, String concertDetailId) {
        ConcertDetail concertDetail = concertDetailJpaRepository.findByConcertIdAndConcertDetailId(concertId, concertDetailId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.CONCERT_NOT_FOUND));

        List<ConcertSeatConfig> seatConfigs = concertSeatConfigJpaRepository.findByIdLessThanEqual(concertDetail.getCapacity());
        return new SeatInfo(seatConfigs);
    }

    @Override public void saveAll(List<Concert> concertList) {
        concertJpaRepository.saveAll(concertList);
    }

    @Override public void detailSaveAll(List<ConcertDetail> concertDetailList) {
        concertDetailJpaRepository.saveAll(concertDetailList);
    }

    @Override public void seatSaveAll(List<ConcertSeatConfig> concertSeatConfigList) {
        concertSeatConfigJpaRepository.saveAll(concertSeatConfigList);
    }

    @Override public Optional<ConcertDetail> getConcertDetailInfo(String concertDetailId) {
        return concertDetailJpaRepository.findByConcertDetailId(concertDetailId);
    }

    @Override public void concertDetailSave(ConcertDetail concertDetail) {
        concertDetailJpaRepository.save(concertDetail);
    }

    @Override public long concertCount() {
        return concertJpaRepository.count();
    }

    @Override public long detailCount() {
        return concertDetailJpaRepository.count();
    }

    @Override public long seatCount() {
        return concertSeatConfigJpaRepository.count();
    }

    @Override
    public void reservationSaveAll(List<ConcertReservation> concertReservations) {
        concertReservationJpaRepository.saveAll(concertReservations);
    }
}