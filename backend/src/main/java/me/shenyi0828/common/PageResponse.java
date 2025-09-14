package me.shenyi0828.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

/**
 * 分页响应包装类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content; // 当前页数据
    private Long totalElements; // 总记录数
    private Integer totalPages; // 总页数
    private Integer number; // 当前页码
    private Integer size; // 每页大小
    private Integer numberOfElements; // 当前页记录数
    private Boolean first; // 是否首页
    private Boolean last; // 是否末页
    private Boolean hasNext; // 是否有下一页
    private Boolean hasPrevious; // 是否有上一页

    // 创建分页响应
    public static <T> PageResponse<T> of(List<T> content, Long totalElements, Integer page, Integer size) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int numberOfElements = content.size();
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return PageResponse.<T>builder()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .number(page)
                .size(size)
                .numberOfElements(numberOfElements)
                .first(first)
                .last(last)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }

    // 创建空分页响应
    public static <T> PageResponse<T> empty(Integer page, Integer size) {
        return PageResponse.<T>builder()
                .content(List.of())
                .totalElements(0L)
                .totalPages(0)
                .number(page)
                .size(size)
                .numberOfElements(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}