package me.shenyi0828.mapper;

import me.shenyi0828.model.TransactionDTO;
import me.shenyi0828.model.TransactionPO;
import me.shenyi0828.model.TransactionEditRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 交易对象转换器
 */
@Mapper
public interface TransactionBeanMapper {

    TransactionBeanMapper INSTANCE = Mappers.getMapper(TransactionBeanMapper.class);

    // PO转DTO
    TransactionDTO poToDto(TransactionPO transactionPO);

    // DTO转PO
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TransactionPO dtoToPo(TransactionDTO transactionDTO);

    // PO列表转DTO列表
    List<TransactionDTO> poListToDtoList(List<TransactionPO> transactionPOList);

    // DTO列表转PO列表
    List<TransactionPO> dtoListToPoList(List<TransactionDTO> transactionDTOList);

    // 请求对象转PO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    TransactionPO requestToPo(TransactionEditRequest transactionEditRequest);

    // 用请求对象更新PO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    void updatePoFromRequest(TransactionEditRequest transactionEditRequest, @MappingTarget TransactionPO transactionPO);

    // 用DTO更新PO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updatePoFromDto(TransactionDTO transactionDTO, @MappingTarget TransactionPO transactionPO);
}