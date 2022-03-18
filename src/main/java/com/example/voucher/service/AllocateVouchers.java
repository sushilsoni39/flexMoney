package com.example.voucher.service;

import com.example.voucher.pojo.TransactionsEntity;
import com.example.voucher.pojo.VoucherAllocationEntity;
import com.example.voucher.pojo.VoucherEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AllocateVouchers {

    public static List<VoucherAllocationEntity> getVouchersListAfterTransaction(List<TransactionsEntity> transactionsEntityDAOList, List<VoucherEntity> voucherEntityList) throws ParseException {
        List<VoucherAllocationEntity> voucherAllocationEntityList = new ArrayList<>();
        List<VoucherEntity> voucherEntitiesAfterRemovingExpired = getVoucherAfterRemovingExpired(voucherEntityList);

        // store in hashmap where key=amount and value=list of vouchers of same amount
        HashMap<Integer, List<VoucherEntity>> voucherEntities = getMappedVouchersWithAmount(voucherEntitiesAfterRemovingExpired);

        //get all unique keys in a list
        ArrayList<Integer> amountList = new ArrayList<>();
        for (int each : voucherEntities.keySet())
            amountList.add(each);

        //sort the keys
        Collections.sort(amountList);

        for (TransactionsEntity transactionsEntity : transactionsEntityDAOList) {
            int transAmount = Integer.parseInt(transactionsEntity.getAmount());

            //when transaction amount matches voucher value
            if (voucherEntities.containsKey(transAmount)) {
                List<VoucherEntity> voucherEntityList1 = voucherEntities.get(transAmount);
                VoucherEntity voucher = voucherEntityList1.get(0);

                String id = transactionsEntity.getTxnId();
                String amount = transactionsEntity.getAmount();
                String voucherAmount = voucher.getAmount();
                String voucherCode = voucher.getVoucherCode();
                voucherAllocationEntityList.add(new VoucherAllocationEntity(id, amount, voucherAmount, voucherCode));

                voucherEntities.get(transAmount).remove(0);
                if (voucherEntities.get(transAmount).size() == 0) voucherEntities.remove(transAmount);
            }

            //when transaction amount does not matches any voucher values
            else
            {
                int len = amountList.size();

                // consume until the net transaction amount is greater the minimum valued voucher
                while(transAmount>=amountList.get(0) && len>0)
                {
                    if(transAmount>=amountList.get(len-1) && voucherEntities.containsKey(amountList.get(len-1)))
                    {
                        List<VoucherEntity> voucherEntityList1 = voucherEntities.get(amountList.get(len-1));
                        VoucherEntity voucher = voucherEntityList1.get(0);

                        String id = transactionsEntity.getTxnId();
                        String amount = transactionsEntity.getAmount();
                        String voucherAmount = voucher.getAmount();
                        String voucherCode = voucher.getVoucherCode();
                        voucherAllocationEntityList.add(new VoucherAllocationEntity(id, amount, voucherAmount, voucherCode));

                        voucherEntities.get(amountList.get(len-1)).remove(0);
                        if (voucherEntities.get(amountList.get(len-1)).size() == 0) voucherEntities.remove(amountList.get(len-1));

                        transAmount=transAmount-amountList.get(len-1);

                    }
                    else len--;
                }

            }

        }

        return voucherAllocationEntityList;
    }


    // compare present date with expiry date of vouchers
    private static boolean compareDate(String present, String expiry) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String[] expiryDate = expiry.split("-");
        String[] presentDate = present.split("-");

        if (Integer.parseInt(expiryDate[0]) > Integer.parseInt(presentDate[0]))
            return true;

        if (Integer.parseInt(expiryDate[0]) == Integer.parseInt(presentDate[0])) {
            if (Integer.parseInt(expiryDate[1]) > Integer.parseInt(presentDate[1]))
                return true;
            else if (Integer.parseInt(expiryDate[1]) == Integer.parseInt(expiryDate[1]))
                if (Integer.parseInt(expiryDate[2]) >= Integer.parseInt(presentDate[2]))
                    return true;
        }

        return false;
    }

    // filtering out only valid vouchers by removing the expired ones
    private static List<VoucherEntity> getVoucherAfterRemovingExpired(List<VoucherEntity> voucherEntityList) throws ParseException {

        List<VoucherEntity> voucherEntitiesAfterRemovingExpiredVouchers = new ArrayList<>();

        for (VoucherEntity voucher : voucherEntityList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String presentDate = sdf.format(date);

            // remove all the voucher which are expired
            Boolean isBefore = compareDate(presentDate, voucher.getExpiry());
            if (isBefore)
                voucherEntitiesAfterRemovingExpiredVouchers.add(voucher);

        }
        return voucherEntitiesAfterRemovingExpiredVouchers;
    }


    // storing vouchers in a map (basically grouping the vouchers with same value)
    private static HashMap<Integer, List<VoucherEntity>> getMappedVouchersWithAmount(List<VoucherEntity> voucherEntitiesAfterRemovingExpired) {
        HashMap<Integer, List<VoucherEntity>> voucherMap = new HashMap<>();
        for (int i = 0; i < voucherEntitiesAfterRemovingExpired.size(); i++) {
            int amount = Integer.parseInt(voucherEntitiesAfterRemovingExpired.get(i).getAmount());
            if (voucherMap.containsKey(amount)) {
                voucherMap.get(amount).add(voucherEntitiesAfterRemovingExpired.get(i));
            } else {
                List<VoucherEntity> voucherEntities = new ArrayList<>();
                voucherEntities.add(voucherEntitiesAfterRemovingExpired.get(i));
                voucherMap.put(amount, voucherEntities);
            }
        }
        return voucherMap;
    }

}


