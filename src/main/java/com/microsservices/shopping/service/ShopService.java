package com.microsservices.shopping.service;

import com.example.shoppingclient.dto.ItemDTO;
import com.example.shoppingclient.dto.ProductDTO;
import com.example.shoppingclient.dto.ShopDTO;
import com.example.shoppingclient.dto.ShopReportDTO;
import com.microsservices.shopping.converter.DTOConverter;
import com.microsservices.shopping.model.Shop;
import com.microsservices.shopping.repository.ReportRepositoryImpl;
import com.microsservices.shopping.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopService {
    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReportRepositoryImpl reportRepository;

    public List<ShopDTO> getAll(){
        List<Shop> shops = shopRepository.findAll();
        return shops
                .stream()
                .map(DTOConverter::convert)
                .collect(Collectors.toList());
    }

    public List<ShopDTO> getByUser(String userIdentifier){
        List<Shop> shops = shopRepository.findAllByUserIdentifier(userIdentifier);
        return shops
                .stream()
                .map(DTOConverter::convert)
                .collect(Collectors.toList());
    }

    public List<ShopDTO> getByDate(ShopDTO shopDTO){
        List<Shop> shops = shopRepository.findAllByDateGreaterThan(shopDTO.getDate());
        return shops
                .stream()
                .map(DTOConverter::convert)
                .collect(Collectors.toList());
    }

    public ShopDTO findById(long ProductId){
        Optional<Shop> shop = shopRepository.findById(ProductId);
        if (shop.isPresent()){
            return DTOConverter.convert(shop.get());
        }
        return null;
    }

    public ShopDTO save(ShopDTO shopDTO){

        if(userService.getUserByCpf(shopDTO.getUserIdentifier()) == null){
            return null;
        }

        shopDTO.setTotal(shopDTO.getItems()
            .stream()
                .map(x -> x.getPrice())
                .reduce((float) 0, Float::sum));

        Shop shop = Shop.convert(shopDTO);
        shop.setDate(new Date());
        shop = shopRepository.save(shop);
        return DTOConverter.convert(shop);
    }

    public List<ShopDTO> getShopByFilter(Date dataInicio, Date dataFim, Float valorMinimo){
        List<Shop> shops = reportRepository.getShopByFilter(dataInicio,dataFim,valorMinimo);
        return shops
                .stream()
                .map(DTOConverter::convert)
                .collect(Collectors.toList());

    }

    public ShopReportDTO getReportByDate(Date dataInicio, Date dataFim){
        return reportRepository.getReportByDate(dataInicio,dataFim);
    }

    private boolean validateProduct(List<ItemDTO> items){
        for(ItemDTO item: items){
            ProductDTO productDTO = productService.getProductByIdentifier(item.getProductIdentifier());
            if(productDTO == null){
                return false;
            }
            item.setPrice(productDTO.getPreco());
        }
        return true;
    }
}
