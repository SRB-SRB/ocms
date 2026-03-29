package com.info.ocms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate=LocalDateTime.now();
    private LocalDateTime dueDate;

    @OneToMany(mappedBy = "assignment",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<AssignmentFile> assignmentFiles=new ArrayList<>();




}
