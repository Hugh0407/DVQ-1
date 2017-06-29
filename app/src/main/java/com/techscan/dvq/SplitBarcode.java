package com.techscan.dvq;

public class SplitBarcode 
{
	public String AccID = "";
	public String cInvCode = "";//物料号
	public String cBatch = "";//批次
	public String cInvName = "";
	public String cSerino = "";//序列号
	public String cBatchStatus = "";
	public String currentBox = "";
	public String TotalBox = "";
	public String CheckNo = "";
	public String FinishBarCode = "";
	public String CheckBarCode = "";

	public String BarcodeType;//条码类型（Y：液态原材料；C：固态单包原材料；TC：固态托盘原材料；P：单包成品；TP托盘成品）
	//private String m_sInventoryCode;//物料号
	//private String m_sLotCode;//批次
	public String TaxFlag;//完税、保税标志
	public Float fQuantity;//重量
	public Integer iNumber;//件数
	public String Outsourcing;//委外
	//public String SeriNo;//序列号
	public String CWFlag;//成品财务标志
	public String OnlyFlag;//成品唯一标识


	public boolean creatorOk=false;

	public SplitBarcode(String sBarcode) throws Exception
	{
		if (sBarcode.equals(""))
			throw new Exception("该条码不符合规则");

		if(!sBarcode.contains("|"))
			throw new Exception("该条码不符合规则");

		String[] lsSplitArray = sBarcode.replace("\n", "").split("|");
		if (lsSplitArray.length < 2)
			throw new Exception("该条码不符合规则");

		BarcodeType = lsSplitArray[0];
		cInvCode = lsSplitArray[1];

		switch (eBarcodeType.getBarcodeType(BarcodeType)) {
			case Y:
				if(lsSplitArray.length != 2)
					throw new Exception("该条码不符合规则");
				break;
			case C:
				if(lsSplitArray.length != 6)
					throw new Exception("该条码不符合规则");
				cBatch = lsSplitArray[2];
				TaxFlag = lsSplitArray[3];
				fQuantity = Float.parseFloat(lsSplitArray[4]);
				cSerino = lsSplitArray[5];
				break;
			case TC:
				if(lsSplitArray.length != 7)
					throw new Exception("该条码不符合规则");
				cBatch = lsSplitArray[2];
				TaxFlag = lsSplitArray[3];
				fQuantity = Float.parseFloat(lsSplitArray[4]);
				iNumber = Integer.parseInt(lsSplitArray[5]);
				cSerino = lsSplitArray[6];
				break;
			case P:
				if(lsSplitArray.length != 9)
					throw new Exception("该条码不符合规则");
				cBatch = lsSplitArray[2];
				Outsourcing = lsSplitArray[3];
				TaxFlag = lsSplitArray[4];
				fQuantity = Float.parseFloat(lsSplitArray[5]);
				CWFlag = lsSplitArray[6];
				OnlyFlag = lsSplitArray[7];
				cSerino = lsSplitArray[8];
				break;
			case TP:
				if(lsSplitArray.length != 10)
					throw new Exception("该条码不符合规则");
				cBatch = lsSplitArray[2];
				Outsourcing = lsSplitArray[3];
				TaxFlag = lsSplitArray[4];
				fQuantity = Float.parseFloat(lsSplitArray[5]);
				iNumber = Integer.parseInt(lsSplitArray[6]);
				CWFlag = lsSplitArray[7];
				OnlyFlag = lsSplitArray[8];
				cSerino = lsSplitArray[9];
				break;
			default:
				throw new Exception("该条码不符合规则");
		}

	}
	private enum eBarcodeType {
		Y,C,TC,P,TP;

		private static eBarcodeType getBarcodeType(String sType){
			return valueOf(sType.toUpperCase());
		}
	}

}
