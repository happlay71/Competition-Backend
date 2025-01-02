package online.happlay.jingsai.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import online.happlay.jingsai.model.query.AwardQuery;
import online.happlay.jingsai.model.query.BaseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class PaginationResultVO<T> {
    private Integer totalCount;
    private Integer pageSize;
    private Integer pageNo;
    private Integer pageTotal;
    private List<T> list = new ArrayList<T>();

    public PaginationResultVO(Integer totalCount, Integer pageSize, Integer pageNo, List<T> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.list = list;
        this.pageNo = pageNo;
    }

    public PaginationResultVO(Integer totalCount, Integer pageSize, Integer pageNo, Integer pageTotal, List<T> list) {
        if (pageNo == 0) {
            pageNo = 1;
        }

        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.list = list;
        this.pageNo = pageNo;
        this.pageTotal = pageTotal;
    }

    public PaginationResultVO(List<T> list) {
        this.list = list;
    }

    public static <T> PaginationResultVO<T> emptyResult(BaseQuery query) {
        if (query == null) {
            query = new BaseQuery();
            query.setPageNo(1); // 默认第一页
            query.setPageSize(10); // 默认每页 10 条
        }

        // 构建空的结果集
        List<T> emptyList = Collections.emptyList();

        // 返回一个通用的空分页结果对象
        return new PaginationResultVO<>(
                0,                  // 总记录数为 0
                query.getPageSize(), // 每页记录数
                query.getPageNo(),   // 当前页号
                0,                  // 总页数为 0
                emptyList           // 空结果集
        );
    }

}