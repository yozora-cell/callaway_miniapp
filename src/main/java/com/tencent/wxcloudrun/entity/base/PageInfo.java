package com.tencent.wxcloudrun.entity.base;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

/**
 * @author yozora
 * Description 分页信息
 **/
@Data
public class PageInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2840351558316760842L;

    /**
     * 当前页页码
     */
    @NotNull(message = "page num can't be null")
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 总页数
     */
    private Integer totalPage = 0;

    /**
     * 总条数
     */
    private Integer totalCount = 0;

    /**
     * 查询起始索引
     */
    protected Integer startIndex = -1;

    /**
     * 功能描述: 判断是否还有数据
     *
     * @param totalCount 总条数
     * @return boolean
     * @author yozora
     */
    public boolean hasData(int totalCount) {
        // 初始化分页条件
        this.checkQueryPageInfo();
        this.totalCount = totalCount;
        this.totalPage = (this.totalCount + this.pageSize - 1) / this.pageSize;
        // 比较是否需要查询数据
        return startIndex < totalCount;
    }

    /**
     * 功能描述: 检查分页参数
     *
     * @author yozora
     */
    protected void checkQueryPageInfo() {
        if (this.pageNum <= 0) {
            throw new IllegalArgumentException("pageNum(" + this.pageNum + ")must more than 0!");
        } else if (this.pageSize <= 0) {
            throw new IllegalArgumentException("pageSize(" + this.pageSize + ")must more than 0!");
        } else {
            this.startIndex = (this.pageNum - 1) * this.pageSize;
            if (this.startIndex < 0) {
                this.startIndex = 0;
            }
        }
    }

}
