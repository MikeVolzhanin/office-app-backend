package com.example.officeappbackend.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Page<T> {
    public ResponseEntity<?> generatePages(List<T> list, Integer page, Integer pageSize){
        int items = list.size();
        int pages = (int) Math.ceil((double) items / pageSize);

        if(pages < page){
            System.out.println("The number of page is less then required");
            return ResponseEntity.ok(new ArrayList<>());
        }

        int fromInd = pageSize * (page - 1);
        int toInd = fromInd + pageSize;

        if(toInd > items)
            toInd = items;

        if(items == 1)
            return ResponseEntity.ok(List.of(list.get(0)));

        return ResponseEntity.ok(list.subList(fromInd, toInd));
    }
}
