package com.info.ocms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="assignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate=LocalDate.now();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="course_id")
    private Course course;

    @OneToMany(mappedBy = "assignment",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<AssignmentFile> assignmentFiles=new ArrayList<>();

    @OneToMany(mappedBy = "assignment",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<SubmittedAssignment> submittedAssignments=new ArrayList<>();


}
