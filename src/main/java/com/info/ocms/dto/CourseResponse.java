package com.info.ocms.dto;

import com.info.ocms.model.CourseFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private List<FileResponse> courseFiles;
}
