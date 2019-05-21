package com.yihuan.exception;

import com.yihuan.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg cm){
        super(cm.toString());
        this.codeMsg = cm;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }
}
