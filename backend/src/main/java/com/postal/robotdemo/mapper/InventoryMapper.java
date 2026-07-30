package com.postal.robotdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.postal.robotdemo.entity.Inventory;
import com.postal.robotdemo.vo.InventoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    /** 联表查询库存+商品名称 */
    @Select("SELECT i.id, i.product_id, p.name AS product_name, " +
            "i.total_quantity, i.locked_quantity, i.available_quantity, " +
            "i.low_threshold, i.sample_missing, i.sample_misplaced " +
            "FROM inventory i LEFT JOIN product p ON i.product_id = p.id " +
            "WHERE i.deleted = 0 ORDER BY i.available_quantity ASC")
    List<InventoryVO> listWithProductName();

    /** 乐观锁扣减库存 */
    @Update("UPDATE inventory SET available_quantity = available_quantity - #{quantity}, " +
            "locked_quantity = locked_quantity + #{quantity}, update_time = NOW() " +
            "WHERE product_id = #{productId} AND available_quantity >= #{quantity}")
    int lockStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    /** 释放锁定库存 */
    @Update("UPDATE inventory SET available_quantity = available_quantity + #{quantity}, " +
            "locked_quantity = locked_quantity - #{quantity}, update_time = NOW() " +
            "WHERE product_id = #{productId} AND locked_quantity >= #{quantity}")
    int releaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    /** 支付成功后实际扣减 */
    @Update("UPDATE inventory SET locked_quantity = locked_quantity - #{quantity}, " +
            "total_quantity = total_quantity - #{quantity}, update_time = NOW() " +
            "WHERE product_id = #{productId} AND locked_quantity >= #{quantity}")
    int deductStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
