package com.example.voucher.pojo;

public class VoucherAllocationEntity {

    private String txnId;
    private String txnAmount;
    private String voucherAmount;
    private String voucherCode;

    public VoucherAllocationEntity(String txnId, String txnAmount, String voucherAmount, String voucherCode)
    {
        this.txnId=txnId;
        this.txnAmount=txnAmount;
        this.voucherAmount=voucherAmount;
        this.voucherCode=voucherCode;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnAmount() {
        return txnAmount;
    }

    public void setTxnAmount(String txnAmount) {
        this.txnAmount = txnAmount;
    }

    public String getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(String voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
