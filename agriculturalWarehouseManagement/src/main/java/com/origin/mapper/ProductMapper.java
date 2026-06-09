package com.origin.mapper;

import com.origin.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Select("SELECT * FROM product WHERE deleted = 0")
    List<Product> findAll();

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product findById(Long id);

    @Select("<script>" +
            "SELECT * FROM product WHERE deleted = 0 " +
            "<if test='category != null and category != \"\"'>AND category = #{category}</if> " +
            "<if test='keyword != null and keyword != \"\"'>AND name LIKE CONCAT('%', #{keyword}, '%')</if> " +
            "</script>")
    List<Product> search(@Param("category") String category, @Param("keyword") String keyword);

    @Insert("INSERT INTO product(name, category, shelf_life_days, storage_condition, unit, quantity, description, status, image) " +
            "VALUES(#{name}, #{category}, #{shelfLifeDays}, #{storageCondition}, #{unit}, #{quantity}, #{description}, #{status}, #{image})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @Update("UPDATE product SET name=#{name}, category=#{category}, shelf_life_days=#{shelfLifeDays}, " +
            "storage_condition=#{storageCondition}, unit=#{unit}, quantity=#{quantity}, description=#{description}, " +
            "status=#{status}, image=#{image} WHERE id=#{id}")
    int update(Product product);

    @Update("UPDATE product SET deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);
}
