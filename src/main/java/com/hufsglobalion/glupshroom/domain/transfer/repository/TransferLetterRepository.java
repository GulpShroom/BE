package com.hufsglobalion.glupshroom.domain.transfer.repository;

import com.hufsglobalion.glupshroom.domain.transfer.entity.TransferLetter;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferLetterRepository extends JpaRepository<TransferLetter, Long> {

    Optional<TransferLetter> findByTransferIdAndSealedFalse(Long transferId);
}
