package com.techscan.dvq;

public class SplitBarcode 
{
	public String AccID = "";
	public String cInvCode = "";
	public String cBatch = "";
	public String cInvName = "";
	public String cSerino = "";
	public String cBatchStatus = "";
	public String currentBox = "";
	public String TotalBox = "";
	public String CheckNo = "";
	public String FinishBarCode = "";
	public String CheckBarCode = "";
	
	public boolean creatorOk=false;
	
	public SplitBarcode(String barcode)
	{
		String[] val;
		CheckNo = "0";
		if(barcode.contains("|"))
		{
			val = barcode.split("\\|");
			if( val.length != 8 && val.length != 7)
			{
				creatorOk=false;
				return;
			}
			
	
			AccID = val[0];
			cInvCode = val[1];
			cBatch = val[2];
			//cSerino = val[3];
			if(val.length==8)
			{
				cInvName= val[3];
				cSerino = val[4];
				if(val.length > 6)
				{
					currentBox = val[6];
					TotalBox = val[7].replace("\n", "");
				}
			}
			else if(val.length==7)
			{				
				cInvName= "";
				cSerino = val[3];
				if(val.length > 5)
				{
					currentBox = val[5];
					TotalBox = val[6].replace("\n", "");
				}
			}
		}
		else if(barcode.contains("\r\n"))
		{
			barcode = barcode.replace("\r\n", "|");
			val = barcode.split("\\|");
			
			if( val.length != 11)
			{
				creatorOk=false;
				return;
			}
			AccID = val[0].replace("账套号:", "");
			cInvCode = val[1].replace("存货编码:", "");
			cBatch = val[3].replace("批次:", "");
			cInvName= val[6].replace("品名:", "");
			cSerino = val[7].replace("流水号:", "");
			if(val.length > 9)
			{
				currentBox = val[9].replace("分包数:", "");
				TotalBox = val[10].replace("\r", "").replace("\n", "").replace("总包装数:", "");
			}
		}
		else if(barcode.contains("\r"))
		{
			barcode = barcode.replace("\r", "|");
			val = barcode.split("\\|");
			
			if( val.length != 11)
			{
				creatorOk=false;
				return;
			}
			AccID = val[0].replace("账套号:", "");
			cInvCode = val[1].replace("存货编码:", "");
			cBatch = val[3].replace("批次:", "");
			cInvName= val[6].replace("品名:", "");
			cSerino = val[7].replace("流水号:", "");
			if(val.length > 9)
			{
				currentBox = val[9].replace("分包数:", "");
				TotalBox = val[10].replace("\r", "").replace("\n", "").replace("总包装数:", "");
			}
		}
		else
		{
			creatorOk=false;
			return;
		}
		creatorOk=true;
		FinishBarCode = AccID + "|" + cInvCode + "|" + 
				cBatch + "|" + cSerino + "|" + CheckNo + "|" + 
				currentBox + "|" + TotalBox;
		CheckBarCode = AccID + "|" + cInvCode + "|" + 
				cBatch + "|" + cSerino + "|" + CheckNo;

	}

}
