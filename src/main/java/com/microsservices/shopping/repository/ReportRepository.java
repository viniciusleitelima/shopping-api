package com.microsservices.shopping.repository;

import com.example.shoppingclient.dto.ShopReportDTO;
import com.microsservices.shopping.model.Shop;

import java.util.Date;
import java.util.List;

public interface ReportRepository {
    public List<Shop> getShopByFilter(Date dataInicio, Date dataFim, Float valorMinimo);
    public ShopReportDTO getReportByDate(Date dataInicio, Date dataFim);

}
