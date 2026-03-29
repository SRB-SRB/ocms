package com.info.ocms.ropository;

import com.info.ocms.model.DocumentMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentMasterRepo extends JpaRepository<DocumentMaster,Long> {

    Optional<DocumentMaster> findByDocumentGuid(String documentGuid);
    Optional<DocumentMaster> deleteByDocumentGuid(String documentGuide);
}
