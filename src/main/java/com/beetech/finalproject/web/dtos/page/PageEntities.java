package com.beetech.finalproject.web.dtos.page;

import lombok.Data;
import org.springframework.data.domain.Pageable;

@Data
public class PageEntities {
    private int size;
    private int number;
}
