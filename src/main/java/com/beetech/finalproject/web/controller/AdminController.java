package com.beetech.finalproject.web.controller;

import com.beetech.finalproject.common.AuthException;
import com.beetech.finalproject.domain.service.AdminService;
import com.beetech.finalproject.domain.service.other.CSVParserService;
import com.beetech.finalproject.domain.service.statistic.ProductStatisticService;
import com.beetech.finalproject.web.common.ResponseDto;
import com.beetech.finalproject.web.dtos.statistic.ProductStatisticDto;
import com.beetech.finalproject.web.dtos.statistic.StatisticInputDto;
import com.beetech.finalproject.web.dtos.user.UserCreateDto;
import com.beetech.finalproject.web.dtos.user.UserDetailDto;
import com.beetech.finalproject.web.response.ProductStatisticResponse;
import com.beetech.finalproject.web.response.UserDetailResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final AdminService adminService;
    private final ProductStatisticService productStatisticService;
    private final CSVParserService csvParserService;

    @GetMapping("/users/")
    public ResponseEntity<ResponseDto<Object>> findAndDisplayUserDetailById(@RequestParam String userId) {
        log.info("request finding user");

        try {
            UserDetailDto userDetailDto = adminService.findAndDisplayUserDetailById(userId);
            UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                    .userDetailDto(userDetailDto)
                    .build();

            return ResponseEntity.ok(ResponseDto.build().withData(userDetailResponse));
        } catch (AuthenticationException e) {
            log.error("Find user failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @PostMapping("/user")
    public ResponseEntity<ResponseDto<Object>> updateUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("request updating user");

        try {
            UserDetailDto userDetailDto = adminService.updateUser(userCreateDto);
            UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                    .userDetailDto(userDetailDto)
                    .build();

            return ResponseEntity.ok(ResponseDto.build().withData(userDetailResponse));
        } catch (AuthenticationException e) {
            log.error("Update user failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @PostMapping("/user/delete-user")
    public ResponseEntity<ResponseDto<Object>> deleteUser(@RequestParam String userId) {
        log.info("request deleting user");

        try {
            UserDetailDto userDetailDto = adminService.deleteUser(userId);
            UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                    .userDetailDto(userDetailDto)
                    .build();

            return ResponseEntity.ok(ResponseDto.build().withData(userDetailResponse));
        } catch (AuthenticationException e) {
            log.error("Delete user failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @PostMapping("/statistic")
    public ResponseEntity<ResponseDto<Object>> getProductStatistic(@RequestBody StatisticInputDto statisticInputDto) {
        log.info("request get statistic product");

        try {
            List<ProductStatisticDto> productStatisticDtoList = new ArrayList<>();

            if(statisticInputDto.getTime().equals("today")) {
                productStatisticDtoList = productStatisticService.getProductStatistic();
            }

            if(statisticInputDto.getTime().equals("week")) {
                productStatisticDtoList = productStatisticService.getProductStatisticByWeek();
            }

            if(statisticInputDto.getTime().equals("month")) {
                productStatisticDtoList = productStatisticService.getProductStatisticByMonth();
            }

            if(statisticInputDto.getTime().equals("year")) {
                productStatisticDtoList = productStatisticService.getProductStatisticByYear();
            }

            List<ProductStatisticResponse> productStatisticResponses = new ArrayList<>();
            ProductStatisticResponse productStatisticResponse = ProductStatisticResponse.builder()
                    .productStatisticDtoList(productStatisticDtoList)
                    .build();

            productStatisticResponses.add(productStatisticResponse);

            return ResponseEntity.ok(ResponseDto.build().withData(productStatisticResponses));
        } catch (AuthenticationException e) {
            log.error("Get statistic product failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }

    @PostMapping("/statistic/export")
    public ResponseEntity<ResponseDto<Object>> exportProductStatistic(@RequestBody StatisticInputDto statisticInputDto,
                                                                      HttpServletResponse response) {
        log.info("request export statistic product");

        String contentDisposition = "Content-Disposition";

        response.setContentType("text/csv");

        if(statisticInputDto.getTime().equals("today")){
            response.setHeader(contentDisposition, "attachment; filename=today-products.csv");
        }

        if(statisticInputDto.getTime().equals("week")){
            response.setHeader(contentDisposition, "attachment; filename=week-products.csv");
        }

        if(statisticInputDto.getTime().equals("month")){
            response.setHeader(contentDisposition, "attachment; filename=month-products.csv");
        }

        if(statisticInputDto.getTime().equals("year")){
            response.setHeader(contentDisposition, "attachment; filename=year-products.csv");
        }

        try {
            Writer writer = response.getWriter();
            csvParserService.exportProductCSVFile(writer, statisticInputDto.getTime(), statisticInputDto.getTitle());
            return ResponseEntity.ok(ResponseDto.build().withMessage("OK"));
        } catch (AuthenticationException | IOException e) {
            log.error("Export statistic product failed: " + e.getMessage());
            throw new AuthException(AuthException.ErrorStatus.INVALID_GRANT);
        }
    }
}
