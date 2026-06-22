package com.SE330_Q22.order_service.client;

import com.SE330_Q22.order_service.client.dto.ProductResponse;
import org.hibernate.annotations.Fetch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient
{
    @GetMapping("/api/product/sku/{skuCode}")
    ProductResponse getProductBySkuCode(@PathVariable String skuCode);
}
