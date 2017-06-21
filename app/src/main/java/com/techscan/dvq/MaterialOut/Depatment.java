package com.techscan.dvq.MaterialOut;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cloverss on 2017/6/21.
 */

public class Depatment {

    public Depatment() {
        department = new ArrayList<department>();
    }

    /**
     * Status : true
     * warehouse : [{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,
     * "addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null",
     * "def6":"null","ts":"2012-10-08 17:19:18","def15":"null","deptduty":"null","def18":"null",
     * "deptname":"董事办","pk_deptdoc":"1011TC100000000000M1","remcode":"null","def1":"null",
     * "def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null",
     * "hrcanceled":"N","innercode":"0J","deptshortname":"null","def17":"null","def11":"null",
     * "deptcode":"1","def16":"null","showorder":"null","maxinnercode":"null",
     * "createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null",
     * "def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null",
     * "def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null",
     * "pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,
     * "addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null",
     * "ts":"2017-05-22 08:48:36","def15":"null","deptduty":"null","def18":"null","deptname":"内审部","pk_deptdoc":"1011TC1000000004KJCA","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"13","deptshortname":"null","def17":"null","def11":"null","deptcode":"101","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2017-05-22","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2017-05-22 08:29:25","def15":"null","deptduty":"null","def18":"null","deptname":"战略发展部","pk_deptdoc":"1011TC1000000004KJC6","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"12","deptshortname":"null","def17":"null","def11":"null","deptcode":"102","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2017-05-22","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:20","def15":"null","deptduty":"null","def18":"null","deptname":"总经办","pk_deptdoc":"1011TC100000000000M3","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"0L","deptshortname":"null","def17":"null","def11":"null","deptcode":"2","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:23","def15":"null","deptduty":"null","def18":"null","deptname":"人力资源部","pk_deptdoc":"1011TC100000000000M6","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"0O","deptshortname":"null","def17":"null","def11":"null","deptcode":"3","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-12 11:51:08","def15":"null","deptduty":"null","def18":"null","deptname":"制品部（1）","pk_deptdoc":"1011TC100000000000LK","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"02","deptshortname":"null","def17":"null","def11":"null","deptcode":"32","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"1011TC100000000000KV","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"6","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-12 11:51:13","def15":"null","deptduty":"null","def18":"null","deptname":"制品部（2）","pk_deptdoc":"1011TC100000000000LL","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"03","deptshortname":"null","def17":"null","def11":"null","deptcode":"33","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"1011TC100000000000KV","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"6","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:04","def15":"null","deptduty":"null","def18":"null","deptname":"销售支持部","pk_deptdoc":"1011TC100000000000LM","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"04","deptshortname":"null","def17":"null","def11":"null","deptcode":"34","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:05","def15":"null","deptduty":"null","def18":"null","deptname":"市场部","pk_deptdoc":"1011TC100000000000LN","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"05","deptshortname":"null","def17":"null","def11":"null","deptcode":"35","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:06","def15":"null","deptduty":"null","def18":"null","deptname":"供应链部","pk_deptdoc":"1011TC100000000000LO","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"06","deptshortname":"null","def17":"null","def11":"null","deptcode":"36","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"},{"pk_corp":"1011","phone":"null","def5":"null","xtersysflag":"null","dr":0,"addr":"null","canceldate":"null","def14":"null","def7":"null","orgtype":"null","def6":"null","ts":"2012-10-08 17:19:07","def15":"null","deptduty":"null","def18":"null","deptname":"香港办","pk_deptdoc":"1011TC100000000000LP","remcode":"null","def1":"null","def19":"null","def20":"null","pk_psndoc":"null","isuseretail":"N","resposition":"null","hrcanceled":"N","innercode":"07","deptshortname":"null","def17":"null","def11":"null","deptcode":"37","def16":"null","showorder":"null","maxinnercode":"null","createdate":"2010-01-01","pk_calbody":"null","depttype":0,"def10":"null","memo":"null","def13":"null","def8":"null","deptattr":"1","canceled":"N","pk_fathedept":"null","def12":"null","def2":"null","pk_psndoc3":"null","deptlevel":"null","def4":"null","def3":"null","def9":"null","pk_psndoc2":"null"}]
     */

    private boolean Status;
    private List<department> department;

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean Status) {
        this.Status = Status;
    }

    public List<department> getWarehouse() {
        return department;
    }

    public void setWarehouse(List<department> warehouse) {
        this.department = warehouse;
    }

    public static class department {
        /**
         * pk_corp : 1011
         * phone : null
         * def5 : null
         * xtersysflag : null
         * dr : 0
         * addr : null
         * canceldate : null
         * def14 : null
         * def7 : null
         * orgtype : null
         * def6 : null
         * ts : 2012-10-08 17:19:18
         * def15 : null
         * deptduty : null
         * def18 : null
         * deptname : 董事办
         * pk_deptdoc : 1011TC100000000000M1
         * remcode : null
         * def1 : null
         * def19 : null
         * def20 : null
         * pk_psndoc : null
         * isuseretail : N
         * resposition : null
         * hrcanceled : N
         * innercode : 0J
         * deptshortname : null
         * def17 : null
         * def11 : null
         * deptcode : 1
         * def16 : null
         * showorder : null
         * maxinnercode : null
         * createdate : 2010-01-01
         * pk_calbody : null
         * depttype : 0
         * def10 : null
         * memo : null
         * def13 : null
         * def8 : null
         * deptattr : 1
         * canceled : N
         * pk_fathedept : null
         * def12 : null
         * def2 : null
         * pk_psndoc3 : null
         * deptlevel : null
         * def4 : null
         * def3 : null
         * def9 : null
         * pk_psndoc2 : null
         */

        private String pk_corp;
        private String phone;
        private String def5;
        private String xtersysflag;
        private int dr;
        private String addr;
        private String canceldate;
        private String def14;
        private String def7;
        private String orgtype;
        private String def6;
        private String ts;
        private String def15;
        private String deptduty;
        private String def18;
        private String deptname;
        private String pk_deptdoc;
        private String remcode;
        private String def1;
        private String def19;
        private String def20;
        private String pk_psndoc;
        private String isuseretail;
        private String resposition;
        private String hrcanceled;
        private String innercode;
        private String deptshortname;
        private String def17;
        private String def11;
        private String deptcode;
        private String def16;
        private String showorder;
        private String maxinnercode;
        private String createdate;
        private String pk_calbody;
        private int depttype;
        private String def10;
        private String memo;
        private String def13;
        private String def8;
        private String deptattr;
        private String canceled;
        private String pk_fathedept;
        private String def12;
        private String def2;
        private String pk_psndoc3;
        private String deptlevel;
        private String def4;
        private String def3;
        private String def9;
        private String pk_psndoc2;

        public String getPk_corp() {
            return pk_corp;
        }

        public void setPk_corp(String pk_corp) {
            this.pk_corp = pk_corp;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDef5() {
            return def5;
        }

        public void setDef5(String def5) {
            this.def5 = def5;
        }

        public String getXtersysflag() {
            return xtersysflag;
        }

        public void setXtersysflag(String xtersysflag) {
            this.xtersysflag = xtersysflag;
        }

        public int getDr() {
            return dr;
        }

        public void setDr(int dr) {
            this.dr = dr;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getCanceldate() {
            return canceldate;
        }

        public void setCanceldate(String canceldate) {
            this.canceldate = canceldate;
        }

        public String getDef14() {
            return def14;
        }

        public void setDef14(String def14) {
            this.def14 = def14;
        }

        public String getDef7() {
            return def7;
        }

        public void setDef7(String def7) {
            this.def7 = def7;
        }

        public String getOrgtype() {
            return orgtype;
        }

        public void setOrgtype(String orgtype) {
            this.orgtype = orgtype;
        }

        public String getDef6() {
            return def6;
        }

        public void setDef6(String def6) {
            this.def6 = def6;
        }

        public String getTs() {
            return ts;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }

        public String getDef15() {
            return def15;
        }

        public void setDef15(String def15) {
            this.def15 = def15;
        }

        public String getDeptduty() {
            return deptduty;
        }

        public void setDeptduty(String deptduty) {
            this.deptduty = deptduty;
        }

        public String getDef18() {
            return def18;
        }

        public void setDef18(String def18) {
            this.def18 = def18;
        }

        public String getDeptname() {
            return deptname;
        }

        public void setDeptname(String deptname) {
            this.deptname = deptname;
        }

        public String getPk_deptdoc() {
            return pk_deptdoc;
        }

        public void setPk_deptdoc(String pk_deptdoc) {
            this.pk_deptdoc = pk_deptdoc;
        }

        public String getRemcode() {
            return remcode;
        }

        public void setRemcode(String remcode) {
            this.remcode = remcode;
        }

        public String getDef1() {
            return def1;
        }

        public void setDef1(String def1) {
            this.def1 = def1;
        }

        public String getDef19() {
            return def19;
        }

        public void setDef19(String def19) {
            this.def19 = def19;
        }

        public String getDef20() {
            return def20;
        }

        public void setDef20(String def20) {
            this.def20 = def20;
        }

        public String getPk_psndoc() {
            return pk_psndoc;
        }

        public void setPk_psndoc(String pk_psndoc) {
            this.pk_psndoc = pk_psndoc;
        }

        public String getIsuseretail() {
            return isuseretail;
        }

        public void setIsuseretail(String isuseretail) {
            this.isuseretail = isuseretail;
        }

        public String getResposition() {
            return resposition;
        }

        public void setResposition(String resposition) {
            this.resposition = resposition;
        }

        public String getHrcanceled() {
            return hrcanceled;
        }

        public void setHrcanceled(String hrcanceled) {
            this.hrcanceled = hrcanceled;
        }

        public String getInnercode() {
            return innercode;
        }

        public void setInnercode(String innercode) {
            this.innercode = innercode;
        }

        public String getDeptshortname() {
            return deptshortname;
        }

        public void setDeptshortname(String deptshortname) {
            this.deptshortname = deptshortname;
        }

        public String getDef17() {
            return def17;
        }

        public void setDef17(String def17) {
            this.def17 = def17;
        }

        public String getDef11() {
            return def11;
        }

        public void setDef11(String def11) {
            this.def11 = def11;
        }

        public String getDeptcode() {
            return deptcode;
        }

        public void setDeptcode(String deptcode) {
            this.deptcode = deptcode;
        }

        public String getDef16() {
            return def16;
        }

        public void setDef16(String def16) {
            this.def16 = def16;
        }

        public String getShoworder() {
            return showorder;
        }

        public void setShoworder(String showorder) {
            this.showorder = showorder;
        }

        public String getMaxinnercode() {
            return maxinnercode;
        }

        public void setMaxinnercode(String maxinnercode) {
            this.maxinnercode = maxinnercode;
        }

        public String getCreatedate() {
            return createdate;
        }

        public void setCreatedate(String createdate) {
            this.createdate = createdate;
        }

        public String getPk_calbody() {
            return pk_calbody;
        }

        public void setPk_calbody(String pk_calbody) {
            this.pk_calbody = pk_calbody;
        }

        public int getDepttype() {
            return depttype;
        }

        public void setDepttype(int depttype) {
            this.depttype = depttype;
        }

        public String getDef10() {
            return def10;
        }

        public void setDef10(String def10) {
            this.def10 = def10;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getDef13() {
            return def13;
        }

        public void setDef13(String def13) {
            this.def13 = def13;
        }

        public String getDef8() {
            return def8;
        }

        public void setDef8(String def8) {
            this.def8 = def8;
        }

        public String getDeptattr() {
            return deptattr;
        }

        public void setDeptattr(String deptattr) {
            this.deptattr = deptattr;
        }

        public String getCanceled() {
            return canceled;
        }

        public void setCanceled(String canceled) {
            this.canceled = canceled;
        }

        public String getPk_fathedept() {
            return pk_fathedept;
        }

        public void setPk_fathedept(String pk_fathedept) {
            this.pk_fathedept = pk_fathedept;
        }

        public String getDef12() {
            return def12;
        }

        public void setDef12(String def12) {
            this.def12 = def12;
        }

        public String getDef2() {
            return def2;
        }

        public void setDef2(String def2) {
            this.def2 = def2;
        }

        public String getPk_psndoc3() {
            return pk_psndoc3;
        }

        public void setPk_psndoc3(String pk_psndoc3) {
            this.pk_psndoc3 = pk_psndoc3;
        }

        public String getDeptlevel() {
            return deptlevel;
        }

        public void setDeptlevel(String deptlevel) {
            this.deptlevel = deptlevel;
        }

        public String getDef4() {
            return def4;
        }

        public void setDef4(String def4) {
            this.def4 = def4;
        }

        public String getDef3() {
            return def3;
        }

        public void setDef3(String def3) {
            this.def3 = def3;
        }

        public String getDef9() {
            return def9;
        }

        public void setDef9(String def9) {
            this.def9 = def9;
        }

        public String getPk_psndoc2() {
            return pk_psndoc2;
        }

        public void setPk_psndoc2(String pk_psndoc2) {
            this.pk_psndoc2 = pk_psndoc2;
        }
    }
}
