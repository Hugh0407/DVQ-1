package com.techscan.dvq;

import android.support.annotation.NonNull;

public class SplitSaleBarcode
{
    public String  P         = "";
    public String  TP        = "";
    public String  cInvCode  = "";
    public String  cBatch    = "";
    @NonNull
    public String  WW        = "";
    @NonNull
    public String  TAX       = "";
    public String  QTY       = "";
    @NonNull
    public String  CW        = "";
    @NonNull
    public String  ONLY      = "";
    public String  cSerino   = "";
    public boolean creatorOk =false;

	public SplitSaleBarcode(@NonNull String barcode) {
		String[] val;

		if (barcode.contains("|")) {
			val = barcode.split("\\|");
			if (val.length != 9 && val.length != 10) {
				creatorOk = false;
				return;
			}

			cInvCode = val[1];
			cBatch = val[2];
			if (val.length == 9) {
				P = val[0];
				QTY = val[5];
				cSerino = val[8];

			} else if (val.length == 10) {
				TP = val[0];

			}
		}


	}
}
