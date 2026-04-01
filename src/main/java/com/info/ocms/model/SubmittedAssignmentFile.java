package com.info.ocms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "submitted_assignment_file")
public class SubmittedAssignmentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String documentGuid;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "submittedAssignment_id")
    private SubmittedAssignment submittedAssignment;
}
