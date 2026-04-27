package com.info.ocms.ropository;

import com.info.ocms.model.DocumentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DocumentMasterRepo extends JpaRepository<DocumentMaster,Long> {

    Optional<DocumentMaster> findByDocumentGuid(String documentGuid);
    void deleteByDocumentGuid(String documentGuide);

}
