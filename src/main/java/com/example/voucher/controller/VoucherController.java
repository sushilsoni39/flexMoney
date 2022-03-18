package com.example.voucher.controller;

import com.example.voucher.csv.CsvUtils;
import com.example.voucher.pojo.TransactionsEntity;
import com.example.voucher.pojo.VoucherAllocationEntity;
import com.example.voucher.pojo.VoucherEntity;
import com.example.voucher.service.AllocateVouchers;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class VoucherController {

    @PostMapping("/voucher_allocation")
    public void uploadTransactions(@RequestParam("file") MultipartFile[] file, HttpServletResponse response) throws IOException {

        List<TransactionsEntity> transactionsEntityDAOList;
        List<VoucherEntity> voucherEntityList;
        try {
            if ((file[0].getOriginalFilename().toLowerCase().contains("transaction"))) {
                transactionsEntityDAOList = CsvUtils.read(TransactionsEntity.class, file[0].getInputStream());
                voucherEntityList = CsvUtils.read(VoucherEntity.class, file[1].getInputStream());
            } else {
                transactionsEntityDAOList = CsvUtils.read(TransactionsEntity.class, file[1].getInputStream());
                voucherEntityList = CsvUtils.read(VoucherEntity.class, file[0].getInputStream());
            }


            String filename = "voucher_allocation.csv";

            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            //create a csv writer
            StatefulBeanToCsv<VoucherAllocationEntity> writer = new StatefulBeanToCsvBuilder<VoucherAllocationEntity>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();

            //write all users to csv file
            writer.write(AllocateVouchers.getVouchersListAfterTransaction(transactionsEntityDAOList, voucherEntityList));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }


}
