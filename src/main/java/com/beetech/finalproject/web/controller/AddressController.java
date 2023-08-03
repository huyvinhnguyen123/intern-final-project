package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.domain.service.CityService;
import com.beetech.finalproject.domain.service.DistrictService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.city.CityDto;
import com.beetech.finalproject.web.dtos.district.DistrictDto;
import com.beetech.finalproject.web.response.CityResponse;
import com.beetech.finalproject.web.response.DistrictResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class AddressController {
    private final CityService cityService;
    private final DistrictService districtService;
    @GetMapping("/categories/cities")
    public ResponseEntity<ResponseDto<Object>> findAllCities() {
        log.info("request finding all cities");

        try {
            List<CityDto> cityDtos = (List<CityDto>) cityService.findAllCities();

            // add result inside response
            List<CityResponse> cityResponses = new ArrayList<>();
            CityResponse cityResponse =  CityResponse.builder()
                    .cities(cityDtos)
                    .build();

            cityResponses.add(cityResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(cityResponses));
        } catch (AuthenticationException e) {
            log.error("Find all cities failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @GetMapping("/categories/districts")
    public ResponseEntity<ResponseDto<Object>> findAllDistrictsByCity(@RequestParam Long cityId) {
        log.info("request finding all districts");

        try {
            List<DistrictDto> districtDtos = (List<DistrictDto>) districtService.findAllDistrictsByCity(cityId);

            // add result inside response
            List<DistrictResponse> districtResponses = new ArrayList<>();
            DistrictResponse districtResponse = DistrictResponse.builder()
                    .districts(districtDtos)
                    .build();

            districtResponses.add(districtResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(districtResponses));
        } catch (AuthenticationException e) {
            log.error("Find all districts failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }
}
