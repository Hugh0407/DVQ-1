package com.techscan.dvq;

/**
 * Created by walter on 2017/6/28.
 */

public class BarcodeDetailInfo {
    private String m_sBarcodeType;//条码类型（Y：液态原材料；C：固态单包原材料；TC：固态托盘原材料；P：单包成品；TP托盘成品）
    private String m_sInventoryCode;//物料号
    private String m_sLotCode;//批次
    private String m_sTaxFlag;//完税、保税标志
    private Float m_fQuantity;//重量
    private Integer m_iNumber;//件数
    private String m_sOutsourcing;//委外
    private String m_sSeriNo;//序列号
    private String m_sCWFlag;//成品财务标志
    private String m_sOnlyFlag;//成品唯一标识

    public String ReBarcodeType(){return m_sBarcodeType;}
    public void SetBarcodeType(String Value){m_sBarcodeType = Value;}

    public String ReInventoryCode(){return m_sInventoryCode;}
    public void SetInventoryCode(String Value){m_sInventoryCode = Value;}

    public String ReLotCode(){return m_sLotCode;}
    public void SetLotCode(String Value){m_sLotCode = Value;}

    public String ReTaxFlag(){return m_sTaxFlag;}
    public void SetTaxFlag(String Value){m_sTaxFlag = Value;}

    public Float ReQuantity(){return m_fQuantity;}
    public void SetQuantity(Float Value){m_fQuantity = Value;}

    public Integer ReNumber(){return m_iNumber;}
    public void SetNumber(Integer Value){m_iNumber = Value;}

    public String ReOutsourcing(){return m_sOutsourcing;}
    public void SetOutsourcing(String Value){m_sOutsourcing = Value;}

    public String ReSeriNo(){return m_sSeriNo;}
    public void SetSeriNo(String Value){m_sSeriNo = Value;}

    public String ReCWFlag(){return m_sCWFlag;}
    public void SetCWFlag(String Value){m_sCWFlag = Value;}

    public String ReOnlyFlag(){return m_sOnlyFlag;}
    public void SetOnlyFlag(String Value){m_sOnlyFlag = Value;}
}
